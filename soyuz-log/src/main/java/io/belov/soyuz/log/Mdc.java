package io.belov.soyuz.log;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by fbelov on 22.11.15.
 */
public class Mdc {

    public static Runnable wrap(Map<String, String> context, Runnable runnable) {
        return () -> {
            try {
                MDC.setContextMap(context);
                runnable.run();
            } finally {
                context.keySet().forEach(MDC::remove);
            }
        };
    }

}
