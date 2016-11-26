package io.belov.soyuz.log;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by fbelov on 22.11.15.
 */
public class Mdc {

    public static void with(Map<String, Object> context, Runnable action) {
        moveDataToMdc(context);

        try {
            action.run();
        } finally {
            removeDataFromMdc(context);
        }
    }

    public static <T> T with(Map<String, Object> context, Callable<T> action) {
        moveDataToMdc(context);

        try {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            removeDataFromMdc(context);
        }
    }

    public static Runnable wrap(Map<String, Object> context, Runnable action) {
        return () -> with(context, action);
    }

    public static <T> Callable<T> wrap(Map<String, Object> context, Callable<T> action) {
        return () -> with(context, action);
    }

    public static void put(String key, String val) throws IllegalArgumentException {
        MDC.put(key, val);
    }

    public static String get(String key) throws IllegalArgumentException {
        return MDC.get(key);
    }

    public static void remove(String key) throws IllegalArgumentException {
        MDC.remove(key);
    }

    public static void clear() {
        MDC.clear();
    }

    private static void moveDataToMdc(Map<String, Object> context) {
        for (Map.Entry<String, Object> e : context.entrySet()) {
            Object value = e.getValue();
            String valueString = (value == null) ? null : value.toString();

            MDC.put(e.getKey(), valueString);
        }
    }

    private static void removeDataFromMdc(Map<String, Object> context) {
        for (String key : context.keySet()) {
            MDC.remove(key);
        }
    }

}
