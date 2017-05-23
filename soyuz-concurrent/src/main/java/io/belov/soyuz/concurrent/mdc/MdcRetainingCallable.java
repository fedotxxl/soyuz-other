package io.belov.soyuz.concurrent.mdc;

import com.google.common.base.Throwables;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Solving logback MDC + ThreadPool problems - http://jira.qos.ch/browse/LOGBACK-422
 */
public class MdcRetainingCallable<V> implements Callable {

    private final Map<String, String> context;
    private final Callable<V> callable;

    public MdcRetainingCallable(Callable<V> callable) {
        this.context = MDC.getCopyOfContextMap();
        this.callable = callable;
    }

    @Override
    public V call() {
        V answer = null;

        Map<String, String> originalContext = MDC.getCopyOfContextMap();
        if (context != null) MDC.setContextMap(context);

        try {
            answer = callable.call();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (originalContext != null) MDC.setContextMap(originalContext);
        }

        return answer;
    }

    public static <V> MdcRetainingCallable<V> cast(Callable<V> c) {
        return new MdcRetainingCallable(c);
    }
}
