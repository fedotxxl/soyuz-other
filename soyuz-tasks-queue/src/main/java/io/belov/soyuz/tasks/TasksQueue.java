package io.belov.soyuz.tasks;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.belov.soyuz.tasks.events.TaskQueueStoppedEvent;
import io.thedocs.soyuz.log.LoggerEvents;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fbelov on 09.02.16.
 */
public class TasksQueue<T> {

    private static final LoggerEvents loge = LoggerEvents.getInstance(TasksQueue.class);

    private TasksQueueConfig config;
    private TasksQueueStorage tasksStorage;
    private TasksQueueContextCreatorI<T> contextCreator;
    private TasksQueueProcessorI<T> processor;
    private TasksQueueToProcessSorterI tasksToProcessSorter;
    private TasksQueueSelectorI selector;
    private List<TasksQueueProcessListenerI<T>> listeners = new ArrayList<>();
    private TransactionOperations transactionTemplate;
    private String eventPrefix;
    private EventBus bus;

    public TasksQueue(TasksQueueContextCreatorI<T> contextCreator, TasksQueueProcessorI<T> processor, TasksQueueToProcessSorterI tasksToProcessSorter, TasksQueueSelectorI selector, TasksQueueStorage tasksStorage, TasksQueueConfig config, TransactionOperations transactionTemplate, EventBus bus) {
        this.contextCreator = contextCreator;
        this.processor = processor;
        this.tasksToProcessSorter = tasksToProcessSorter;
        this.selector = selector;
        this.tasksStorage = tasksStorage;
        this.config = config;
        this.transactionTemplate = transactionTemplate;
        this.bus = bus;

        setUp();

        if (config.isStartOnCreation()) {
            start();
        }
    }

    private volatile boolean mustStop = false;
    private BoundedExecutor executor;
    private Thread workerThread;

    private void setUp() {
        String threadNamePrefix = "tq" + ((config.hasQueueName()) ? "." + config.getQueueName() : "");
        ExecutorService exec = Executors.newFixedThreadPool(config.getMaxTasksToProcessAtTheSameTime(), ThreadUtils.withPrefix(threadNamePrefix));

        executor = new BoundedExecutor(exec, config.getMaxTasksToProcessAtTheSameTime());
        eventPrefix = "tq." + ((config.hasQueueName()) ? config.getQueueName() + "." : "");
    }

    public void setListener(TasksQueueProcessListenerI<T> listener) {
        this.listeners = ImmutableList.of(listener);
    }

    public void setListeners(List<TasksQueueProcessListenerI<T>> listeners) {
        this.listeners = listeners;
    }

    public synchronized void start() {
        if (workerThread != null) {
            workerThread.interrupt();
        }

        restartTasksMarkedAsInProcess();

        mustStop = false;
        workerThread = new Thread(this::acquireAndSchedule, eventPrefix + "queueWatcher");
        workerThread.start();

        loge.info(getEventName("started"));
    }

    public synchronized void stop() {
        mustStop = true;

        loge.debug(getEventName("stop.start"));

        try {
            Thread.sleep(200);

            if (workerThread != null && workerThread.isAlive()) {
                Thread.sleep(config.getDelayBeforeInterruptingWorkerThread());

                if (workerThread.isAlive()) {
                    workerThread.interrupt();
                    Thread.sleep(1000);
                }
            }

            workerThread = null;

            loge.info(getEventName("stop.waitingForWorkers"));

            executor.shutdown();

            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }

            loge.info(getEventName("stop.success"));
        } catch (InterruptedException e) {
            loge.warn(getEventName("stop.failure"), ImmutableMap.of("e", e.toString()));
        }
    }

    private void acquireAndSchedule() {
        Task task = null;

        try {
            while (!mustStop) {
                int threadDelay;

                if (executor.canScheduleMore()) {
                    task = acquire();

                    if (task == null) {
                        threadDelay = config.getDelayOnEmpty();
                    } else {
                        scheduleToProcess(task);
                        threadDelay = config.getDelayOnTask();
                    }
                } else {
                    threadDelay = config.getDelayOverflow();
                }

                task = null;
                Thread.sleep(threadDelay);
            }
        } catch (InterruptedException ie) {
            loge.info(getEventName("interrupted"));
            if (task != null) release(task, TasksQueueProcessorI.Result.REPEAT_NOW);
        } catch (Exception e) {
            loge.error(getEventName("e"), e);
            if (task != null) release(task, TasksQueueProcessorI.Result.EXCEPTION);
        } finally {
            bus.post(new TaskQueueStoppedEvent(config.getQueueName()));
        }
    }

    private Task acquire() {
        return transactionTemplate.execute((c) -> {
            Task answer = null;
            List<Task> tasks = tasksToProcessSorter.sort(tasksStorage.findAllToProcess(config.getTaskType()));

            if (CollectionUtils.isNotEmpty(tasks)) {
                answer = selector.select(tasks);
            }

            if (answer != null) {
                tasksStorage.markAsQueuedAndSetStatus(answer.getId(), Task.Status.IN_PROGRESS);
            }

            return answer;
        });
    }

    private void restartTasksMarkedAsInProcess() {
        transactionTemplate.execute((c) -> {
            List<Integer> taskIds = tasksStorage.restartTasksMarkedAsInProcess(config.getTaskType());

            if (CollectionUtils.isNotEmpty(taskIds)) {
                loge.info(getEventName("restartedTasksMarkedAsInProcess"), ImmutableMap.of("ids", taskIds));
            }

            return taskIds;
        });
    }

    private void scheduleToProcess(Task task) throws InterruptedException {
        executor.submitTask(Mdc.wrap(ImmutableMap.of("t", task.getId()), () -> {
            TasksQueueProcessorI.Result result = TasksQueueProcessorI.Result.EXCEPTION;

            try {
                result = process(task);
            } catch (Throwable e) {
                loge.error(getEventName("process.e"), e);
            } finally {
                release(task, result);
            }
        }));
    }

    private TasksQueueProcessorI.Result process(Task task) {
        listeners.forEach(l -> {
            if (l instanceof TasksQueueProcessListenerI.Start) {
                ((TasksQueueProcessListenerI.Start) l).onStart(task);
            }
        });

        AtomicReference<TasksQueueProcessorI.Result> result = new AtomicReference<>(TasksQueueProcessorI.Result.EXCEPTION);
        T executionContext = contextCreator.createContext(task);

        try {
            result.set(
                    executeWithinTransactionIfNecessary((c) -> {
                        loge.debug(getEventName("process.start"), ImmutableMap.of("t", task.getId()));

                        TasksQueueProcessorI.Result processResult = processor.process(task, executionContext);
                        AtomicReference<TasksQueueProcessorI.Result> answer = new AtomicReference<>(processResult);

                        listeners.forEach(l -> l.on(task, executionContext, answer));

                        return answer;
                    }).get()
            );

            for (TasksQueueProcessListenerI l : listeners) {
                if (l instanceof TasksQueueProcessListenerI.AfterTransaction) {
                    ((TasksQueueProcessListenerI.AfterTransaction<T>) l).onAfterTransaction(task, executionContext, result);
                }
            }
        } catch (Throwable e) {
            listeners.forEach(l -> l.onException(task, executionContext, e));

            result.set(TasksQueueProcessorI.Result.EXCEPTION);
        } finally {
            listeners.forEach(l -> {
                if (l instanceof TasksQueueProcessListenerI.Finally) {
                    ((TasksQueueProcessListenerI.Finally<T>) l).onFinally(task, executionContext);
                }
            });

            loge.debug(getEventName("process.finish"), ImmutableMap.of("t", task.getId(), "result", result));
        }

        return result.get();
    }

    private <T> T executeWithinTransactionIfNecessary(TransactionCallback<T> action) throws TransactionException {
        if (config.isDoNotUseTransactionOnProcessing()) {
            return action.doInTransaction(null);
        } else {
            return transactionTemplate.execute(action);
        }
    }

    private void release(Task task, TasksQueueProcessorI.Result result) {
        if (result == TasksQueueProcessorI.Result.REPEAT_NOW) {
            tasksStorage.markToRepeatNow(task.getId());
        } else {
            tasksStorage.markAsQueuedAndSetStatus(task.getId(), getStatusForResult(result));
        }
    }

    private Task.Status getStatusForResult(TasksQueueProcessorI.Result result) {
        if (result == TasksQueueProcessorI.Result.SUCCESS || result == TasksQueueProcessorI.Result.SKIP) {
            return Task.Status.SUCCESS;
        } else if (result == TasksQueueProcessorI.Result.FAILURE) {
            return Task.Status.FAILURE;
        } else if (result == TasksQueueProcessorI.Result.REPEAT) {
            return Task.Status.NEW;
        } else if (result == TasksQueueProcessorI.Result.EXCEPTION) {
            return Task.Status.EXCEPTION;
        } else {
            throw new IllegalStateException();
        }
    }

    private String getEventName(String postfix) {
        return eventPrefix + postfix;
    }

    /**
     * http://stackoverflow.com/questions/2001086/how-to-make-threadpoolexecutors-submit-method-block-if-it-is-saturated
     */
    private static class BoundedExecutor {

        private static final LoggerEvents loge = LoggerEvents.getInstance(BoundedExecutor.class);

        private final ExecutorService exec;
        private final Semaphore semaphore;

        public BoundedExecutor(ExecutorService exec, int bound) {
            this.exec = exec;
            this.semaphore = new Semaphore(bound);
        }

        public synchronized void submitTask(final Runnable command) throws InterruptedException, RejectedExecutionException {
            semaphore.acquire();

            try {
                exec.execute(() -> {
                    try {
                        command.run();
                    } catch (Throwable e) {
                        loge.error("be.e", e);
                    } finally {
                        semaphore.release();
                    }
                });
            } catch (RejectedExecutionException e) {
                semaphore.release();
                throw e;
            }
        }

        public synchronized boolean canScheduleMore() {
            return semaphore.availablePermits() > 0;
        }

        public void shutdown() {
            exec.shutdown();
        }

        public boolean isTerminated() {
            return exec.isTerminated();
        }
    }

    /**
     * Created by fbelov on 22.11.15.
     */
    private static class Mdc {

        public static void with(Map<String, Object> context, Runnable action) {
            moveDataToMdc(context);

            try {
                action.run();
            } finally {
                removeDataFromMdc(context);
            }
        }

        @SneakyThrows
        public static <T> T with(Map<String, Object> context, Callable<T> action) {
            moveDataToMdc(context);

            try {
                return action.call();
            } finally {
                removeDataFromMdc(context);
            }
        }

        public static Runnable wrap(Map<String, Object> context, Runnable action) {
            return () -> with(context, action);
        }

        public static <T> Callable<T> wrap(Map<String, Object> context, Callable<T> action) {
            return () -> with(context, action);
        }

        public static void put(String key, String val) throws IllegalArgumentException {
            MDC.put(key, val);
        }

        public static String get(String key) throws IllegalArgumentException {
            return MDC.get(key);
        }

        public static void remove(String key) throws IllegalArgumentException {
            MDC.remove(key);
        }

        public static void clear() {
            MDC.clear();
        }

        private static void moveDataToMdc(Map<String, Object> context) {
            for (Map.Entry<String, Object> e : context.entrySet()) {
                Object value = e.getValue();
                String valueString = (value == null) ? null : value.toString();

                MDC.put(e.getKey(), valueString);
            }
        }

        private static void removeDataFromMdc(Map<String, Object> context) {
            for (String key : context.keySet()) {
                MDC.remove(key);
            }
        }

    }

    private static class ThreadUtils {

        private static final ExecutorService POOL = Executors.newCachedThreadPool();
        private static final Random RANDOM = new Random();

        public static ThreadFactory withName(String name) {
            return new ThreadFactoryBuilder().setNameFormat(name).build();
        }

        public static ThreadFactory withPrefix(String prefix) {
            return withName(prefix + "-%d");
        }

        public static void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw Throwables.propagate(e);
            }
        }

        public static void randomSleep(Integer max) {
            sleep(RANDOM.nextInt(max));
        }

        public static Future<?> timeout(long millis, Runnable runnable) {
            return POOL.submit(() -> {
                sleep(millis);
                runnable.run();
            });
        }

        public static <T> Future<T> timeout(long millis, Callable<T> callable) {
            return POOL.submit(() -> {
                sleep(millis);
                return callable.call();
            });
        }

    }

}
