package io.belov.soyuz.concurrent;

import com.google.common.collect.Sets;
import io.belov.soyuz.log.Mdc;
import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by fbelov on 17.11.15.
 */
public class ProcessorPool<T> {

    private static final LoggerEvents loge = LoggerEvents.getInstance(ProcessorPool.class);

    private List<T> processingActions = Collections.synchronizedList(new ArrayList<T>());
    private Map<T, Future> futuresByActions = new ConcurrentHashMap<>();
    private LockerMap<String> lockers = new LockerMap<>();
    private Set<String> scheduled = Sets.newConcurrentHashSet();
    private Consumer<T> processor;
    private Semaphore semaphore;
    private ExecutorService executor;

    public ProcessorPool(int maxThreadsCount, Consumer<T> processor) {
        this.processor = processor;
        this.semaphore = new Semaphore(maxThreadsCount, true);
        this.executor = Executors.newFixedThreadPool(maxThreadsCount);
    }

    public void process(T action) {
        process(null, action);
    }

    public void process(@Nullable String actionId, T action) {
        try {
            semaphore.acquire();

            if (actionId != null) {
                AtomicBoolean isScheduled = new AtomicBoolean(false);

                lockers.lockAndDo(actionId, () -> {
                    if (!scheduled.contains(actionId)) {
                        scheduled.add(actionId);
                        isScheduled.set(true);
                    }
                });

                if (isScheduled.get()) {
                    try {
                        processingActions.add(action);
                        doProcess(actionId, action);
                    } finally {
                        scheduled.remove(actionId);
                        processingActions.remove(action);
                    }
                }
            } else {
                doProcess(actionId, action);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
    }

    public void interrupt(T action) {
        Future future = futuresByActions.get(action);

        if (future != null) {
            future.cancel(true);
        }
    }

    public List<T> getProcessingActions() {
        return processingActions;
    }

    private void doProcess(@Nullable String actionId, T action) {
        try {
            Future future = executor.submit(() -> {
                try {
                    Runnable rProcess = () -> processor.accept(action);

                    if (actionId == null) {
                        rProcess.run();
                    } else {
                        Mdc.wrap(to.map("a", actionId), rProcess).run();
                    }
                } catch (Exception e) {
                    loge.error("processorPool.e", to.map("id", actionId, "a", action), e);
                }
            });

            futuresByActions.put(action, future);

            future.get();
        } catch (InterruptedException | CancellationException e) {
            loge.warn("processorPool.interrupted", to.map("id", actionId, "a", action));
        } catch (Exception e) {
            loge.error("processorPool.e", to.map("id", actionId, "a", action), e);
        } finally {
            futuresByActions.remove(action);
        }
    }
}
