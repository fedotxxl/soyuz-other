package io.belov.soyuz.queue;

import org.bson.types.ObjectId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fbelov on 29.05.15.
 */
public class QueueSynchronizer {

    private int maxItemsPerProject;
    private Map<ObjectId, Object> locks = new ConcurrentHashMap<>();
    private Map<ObjectId, AtomicInteger> processing = new ConcurrentHashMap<>();
    private AtomicInteger processingItemsCount = new AtomicInteger(0);

    public QueueSynchronizer() {
        this.maxItemsPerProject = 1;
    }

    public QueueSynchronizer(int maxItemsPerProject) {
        //this will not work since actions are not marked as processing
        //next action pull will select action which are already in process and start processing them again
        //this.maxItemsPerProject = maxItemsPerProject;

        this.maxItemsPerProject = 1;
    }

    public Object getLockForProject(ObjectId projectId) {
        Object answer = locks.get(projectId);

        if (answer == null) {
            synchronized (this) {
                answer = locks.get(projectId);
                if (answer == null) {
                    answer = new Object();
                    locks.put(projectId, answer);
                }
            }
        }

        return answer;
    }

    public void markAsProcessing(ObjectId projectId) {
        getProcessing(projectId).incrementAndGet();
        processingItemsCount.incrementAndGet();
    }

    public void markAsProcessed(ObjectId projectId) {
        getProcessing(projectId).decrementAndGet();
        processingItemsCount.decrementAndGet();
    }

    public boolean isProcessing(ObjectId projectId) {
        return getProcessing(projectId).get() >= maxItemsPerProject;
    }

    private synchronized AtomicInteger getProcessing(ObjectId projectId) {
        AtomicInteger answer = processing.get(projectId);

        if (answer == null) {
            answer = new AtomicInteger();
            processing.put(projectId, answer);
        }

        return answer;
    }

    public boolean isProcessing() {
        return processingItemsCount.get() > 0;
    }
}
