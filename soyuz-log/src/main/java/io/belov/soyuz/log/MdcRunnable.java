package io.belov.soyuz.log;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbelov on 08.09.15.
 */
public class MdcRunnable implements Runnable {

    private Map<String, Object> context;
    private Runnable runnable;

    public MdcRunnable(String key, String value, Runnable runnable) {
        this.runnable = runnable;
        this.context = new HashMap<>();
        this.context.put(key, value);
    }

    public MdcRunnable(Map<String, Object> context, Runnable runnable) {
        this.context = context;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        Mdc.wrap(context, runnable).run();
    }
}
