package io.belov.soyuz.concurrent.mdc;

import com.google.common.base.Throwables;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Solving logback MDC + ThreadPool problems - http://jira.qos.ch/browse/LOGBACK-422
 */
public class MdcRetainingRunnable implements Runnable {

    private final Map<String, String> context;
    private final Runnable runnable;

    public MdcRetainingRunnable(Runnable runnable) {
        this.context = MDC.getCopyOfContextMap();
        this.runnable = runnable;
    }

    @Override
    public void run() {
        Map<String, String> originalContext = MDC.getCopyOfContextMap();
        if (context != null) MDC.setContextMap(context);

        try {
            runnable.run();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            if (originalContext != null) MDC.setContextMap(originalContext);
        }
    }

    public static MdcRetainingRunnable cast(Runnable r) {
        return new MdcRetainingRunnable(r);
    }

}
