package io.belov.soyuz.concurrent;

import io.belov.soyuz.log.LoggerEvents;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * http://stackoverflow.com/questions/2001086/how-to-make-threadpoolexecutors-submit-method-block-if-it-is-saturated
 */
public class BoundedExecutor {

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