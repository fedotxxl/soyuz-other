package io.belov.soyuz.concurrent;

import com.google.common.base.Throwables;
import io.belov.soyuz.log.Mdc;
import io.belov.soyuz.utils.to;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * Created by fbelov on 17.11.15.
 */
public class ProcessorPool<T> {

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

    public void process(String actionId, T action) {
        try {
            semaphore.acquire();

            executor.submit(() -> {
                Runnable runnable = () -> processor.accept(action);

                if (actionId == null) {
                    runnable.run();
                } else {
                    Mdc.wrap(to.map("a", actionId), runnable).run();
                }
            }).get();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            semaphore.release();
        }
    }
}
