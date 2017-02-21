package io.belov.soyuz.concurrent;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by fbelov on 23.09.15.
 */
public class ThreadUtils {

    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Random RANDOM = new Random();

    public static ThreadFactory withName(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static ThreadFactory withPrefix(String prefix) {
        return withName(prefix + "-%d");
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

    public static void randomSleep(Integer max) {
        sleep(RANDOM.nextInt(max));
    }

    public static Future<?> timeout(long millis, Runnable runnable) {
        return POOL.submit(() -> {
            sleep(millis);
            runnable.run();
        });
    }

    public static <T> Future<T> timeout(long millis, Callable<T> callable) {
        return POOL.submit(() -> {
            sleep(millis);
            return callable.call();
        });
    }

}
