package io.belov.soyuz.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fbelov on 17.11.15.
 */
public class LockerMap<T> {

    private Map<T, Lock> locksByProjectIds = new HashMap<>();

    public synchronized Lock get(T key) {
        Lock answer = locksByProjectIds.get(key);

        if (answer == null) {
            answer = new ReentrantLock();
            locksByProjectIds.put(key, answer);
        }

        return answer;
    }

}
