package io.belov.soyuz.guava;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Created by fbelov on 23.05.15.
 */
public class GuavaUtils {

    public static <T> Optional<T> getFirst(Iterable<? extends T> iterable) {
        return Optional.fromNullable(Iterables.getFirst(iterable, null));
    }

}
