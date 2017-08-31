package io.belov.soyuz.concurrent;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created on 31.08.17.
 */
public class Locker<T> {

    private ReentrantLock lock = new ReentrantLock();
    private volatile T lockedKey;
    private int tryDelayInMillis;
    private LockFailureAction<T> lockFailureAction;

    public Locker(int tryDelayInMillis, LockFailureAction<T> lockFailureAction) {
        this.tryDelayInMillis = tryDelayInMillis;
        this.lockFailureAction = lockFailureAction;
    }

    @SneakyThrows
    public void lockAndDo(T key, Consumer<T> action) {
        lockAndDo(key, (k) -> {
            action.accept(k);

            return -1;
        });
    }

    @SneakyThrows
    public void lockAndDo(T key, Runnable action) {
        lockAndDo(key, (k) -> {
            action.run();

            return -1;
        });
    }

    @SneakyThrows
    public <R> R lockAndDo(T key, Function<T, R> action) {
        R answer = null;
        boolean locked = false;

        while (!locked) {
            locked = lock.tryLock(tryDelayInMillis, TimeUnit.MILLISECONDS);

            if (locked) {
                onLockSuccess(key);

                try {
                    answer = action.apply(key);
                } finally {
                    lock.unlock();
                    onUnlock();
                }
            } else {
                onLockFailure(key);
            }
        }

        return answer;
    }

    private void onLockSuccess(T key) {
        lockedKey = key;
    }

    private void onLockFailure(T key) {
        lockFailureAction.onFailure(lockedKey, key);
    }

    private void onUnlock() {
        lockedKey = null;
    }

    public interface LockFailureAction<T> {
        void onFailure(T lockedKey, T tryKey);
    }

}
