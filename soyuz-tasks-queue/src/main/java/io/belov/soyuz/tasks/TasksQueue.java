package io.belov.soyuz.tasks;

import io.belov.soyuz.concurrent.BoundedExecutor;
import io.belov.soyuz.concurrent.ThreadUtils;
import io.belov.soyuz.log.LoggerEvents;
import io.belov.soyuz.log.Mdc;
import io.belov.soyuz.tasks.events.TaskQueueStoppedEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        AtomicReference<TasksQueueProcessorI.Result> result = new AtomicReference<>(TasksQueueProcessorI.Result.EXCEPTION);
        T executionContext = contextCreator.createContext(task);

        try {
            result.set(
                    transactionTemplate.execute((c) -> {
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
}
