package io.belov.soyuz.concurrent;

import com.google.common.base.Throwables;

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
        try {
            semaphore.acquire();

            executor.submit(() -> processor.accept(action)).get();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            semaphore.release();
        }
    }
}
