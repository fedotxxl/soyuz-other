package io.belov.soyuz.log;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbelov on 08.09.15.
 */
public class MdcRunnable implements Runnable {

    private Map<String, String> context;
    private Runnable runnable;

    public MdcRunnable(String key, String value, Runnable runnable) {
        this.runnable = runnable;
        this.context = new HashMap<>();
        this.context.put(key, value);
    }

    public MdcRunnable(Map<String, String> context, Runnable runnable) {
        this.context = context;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            MDC.setContextMap(context);
            runnable.run();
        } finally {
            context.keySet().forEach(MDC::remove);
        }
    }
}
