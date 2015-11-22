package io.belov.soyuz.concurrent;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import io.belov.soyuz.log.Mdc;
import io.belov.soyuz.utils.to;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by fbelov on 17.11.15.
 */
public class ProcessorPool<T> {

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
                        doProcess(actionId, action);
                    } finally {
                        scheduled.remove(actionId);
                    }
                }
            } else {
                doProcess(actionId, action);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            semaphore.release();
        }
    }

    private void doProcess(@Nullable String actionId, T action) {
        try {
            executor.submit(() -> {
                Runnable rProcess = () -> processor.accept(action);

                if (actionId == null) {
                    rProcess.run();
                } else {
                    Mdc.wrap(to.map("a", actionId), rProcess).run();
                }
            }).get();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
