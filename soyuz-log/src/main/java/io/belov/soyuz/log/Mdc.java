package io.belov.soyuz.log;

import lombok.SneakyThrows;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by fbelov on 22.11.15.
 */
public class Mdc {

    public static void with(Map<String, Object> context, Runnable action) {
        List<String> movedKeys = moveDataToMdc(context);

        try {
            action.run();
        } finally {
            removeDataFromMdc(context, movedKeys);
        }
    }

    @SneakyThrows
    public static <T> T with(Map<String, Object> context, Callable<T> action) {
        List<String> movedKeys = moveDataToMdc(context);

        try {
            return action.call();
        } finally {
            removeDataFromMdc(context, movedKeys);
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

    private static List<String> moveDataToMdc(Map<String, Object> context) {
        List<String> movedKeys = new ArrayList<>();

        for (Map.Entry<String, Object> e : context.entrySet()) {
            String key = e.getKey();

            if (MDC.get(key) == null) {
                Object value = e.getValue();
                String valueString = (value == null) ? null : value.toString();

                MDC.put(key, valueString);
                movedKeys.add(key);
            }
        }

        return movedKeys;
    }

    private static void removeDataFromMdc(Map<String, Object> context, List<String> movedKeys) {
        for (String key : movedKeys) {
            MDC.remove(key);
        }
    }

}
