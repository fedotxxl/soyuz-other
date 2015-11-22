package io.belov.soyuz.concurrent;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Created by fbelov on 17.11.15.
 */
public class LockerMap<T> {

    private Map<T, Lock> locksByProjectIds = new ConcurrentHashMap<>();

    public synchronized Lock get(T key) {
        Lock answer = locksByProjectIds.get(key);

        if (answer == null) {
            answer = new ReentrantLock();
            locksByProjectIds.put(key, answer);
        }

        return answer;
    }

    public void lockAndDo(T key, Runnable action) {
        Lock lock = get(key);

        try {
            lock.lock();

            action.run();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    public void lockAndDo(T key, Consumer<T> action) {
        Lock lock = get(key);

        try {
            lock.lock();

            action.accept(key);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    public <V> V lockAndDo(T key, Callable<V> action) {
        Lock lock = get(key);

        try {
            lock.lock();

            return action.call();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    public <V> V lockAndDo(T key, Function<T, V> action) {
        Lock lock = get(key);

        try {
            lock.lock();

            return action.apply(key);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }
}
