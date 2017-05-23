package io.belov.soyuz.concurrent;

import com.google.common.util.concurrent.AbstractListeningExecutorService;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Copied directly from guava implementation https://github.com/google/guava/blob/master/guava/src/com/google/common/util/concurrent/MoreExecutors.java#L514
 * in order to add custom logback {@link MDC} context information for threads that don't have it
 * set automatically such as normal servlet request threads.
 *
 * Also helped by example <a href="http://stackoverflow.com/a/19329668/571885">here</a>.
 */
public class MdcExecutorService extends AbstractListeningExecutorService {
    private final ExecutorService delegate;

    public MdcExecutorService(ExecutorService delegate) {
        this.delegate = checkNotNull(delegate);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(wrap(command, MDC.getCopyOfContextMap()));
    }

    /*
    Copied close to literally directly from http://stackoverflow.com/a/19329668/571885
     */
    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return new Runnable() {
            @Override
            public void run() {
                Map<String, String> previous = MDC.getCopyOfContextMap();

                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }

                try {
                    runnable.run();
                } finally {
                    if (previous == null) {
                        MDC.clear();
                    } else {
                        MDC.setContextMap(previous);
                    }
                }
            }
        };
    }
}