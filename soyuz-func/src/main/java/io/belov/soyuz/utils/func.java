package io.belov.soyuz.utils;

import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by fbelov on 06.07.16.
 */
public class func {

    public static <T> T consume(T object, Consumer<T> consumer) {
        consumer.accept(object);

        return object;
    }
    
    public static <T> T checkAndConsume(T object, Consumer<T> consumer) {
        if (check(object)) {
            consume(object, consumer);
        }

        return object;
    }

    @SneakyThrows
    public static <T> T call(Callable<T> callable) {
        return callable.call();
    }

    public static <T, R> R apply(T object, Function<T, R> consumer) {
        return consumer.apply(object);
    }

    @Nullable
    public static <T, R> R checkAndApply(T object, Function<T, R> consumer) {
        if (check(object)) {
            return apply(object, consumer);
        } else {
            return null;
        }
    }

    private static <T> boolean check(T object) {
        if (object == null) {
            return false;
        } else {
            if (object instanceof String) {
                return is.t((String) object);
            } else if (object instanceof Boolean) {
                return is.t((Boolean) object);
            } else if (object instanceof Collection) {
                return is.t((Collection) object);
            } else if (object instanceof Object[]) {
                return is.t((Object[]) object);
            } else if (object instanceof Map) {
                return is.t((Map) object);
            } else {
                return true;
            }
        }
    }
    
}
