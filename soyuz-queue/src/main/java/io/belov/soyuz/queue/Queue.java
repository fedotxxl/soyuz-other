package io.belov.soyuz.queue;

import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by fbelov on 28.05.15.
 */
public class Queue {

    private Map<ObjectId, LinkedHashSet<QueueAction>> queues = new ConcurrentHashMap<>();

    private String name;
    private int maxQueueSizePerProject;
    private QueueSynchronizer sync;
    private List<Consumer<QueueAction>> processedListeners = new ArrayList<>();
    private volatile Iterator<ObjectId> iterator;

    public Queue(String name, int maxQueueSizePerProject) {
        this(name, maxQueueSizePerProject, new QueueSynchronizer());
    }

    public Queue(String name, int maxQueueSizePerProject, QueueSynchronizer sync) {
        this.name = name;
        this.maxQueueSizePerProject = maxQueueSizePerProject;
        this.sync = sync;
    }

    public String getName() {
        return name;
    }

    public void setSynchronizer(QueueSynchronizer sync) {
        this.sync = sync;
    }

    public boolean push(QueueAction queueAction) {
        synchronized (sync.getLockForProject(queueAction.getProjectId())) {
            Set<QueueAction> queue = getQueueForProject(queueAction.getProjectId());
            if (queue.size() < maxQueueSizePerProject) {
                queue.add(queueAction);

                return true;
            } else {
                return false;
            }
        }
    }

    public synchronized QueueAction nextToProcess() {
        if (iterator == null || !iterator.hasNext()) iterator = queues.keySet().iterator();

        while (iterator.hasNext()) {
            ObjectId projectId = iterator.next();

            synchronized (sync.getLockForProject(projectId)) {
                if (!sync.isProcessing(projectId)) {
                    QueueAction action = getFirst(getQueueForProject(projectId));
                    if (action != null) {
                        sync.markAsProcessing(projectId);
                        return action;
                    }
                }
            }
        }

        iterator = null;

        return null;
    }

    public Set<QueueAction> getActions(ObjectId projectId) {
        synchronized (sync.getLockForProject(projectId)) {
            return getQueueForProject(projectId);
        }
    }

    public void processed(ObjectId projectId) {
        synchronized (sync.getLockForProject(projectId)) {
            LinkedHashSet<QueueAction> queue = getQueueForProject(projectId);
            Iterator<QueueAction> iterator = queue.iterator();

            if (iterator.hasNext()) {
                QueueAction action = iterator.next();
                processedListeners.forEach(l -> l.accept(action));
                iterator.remove();
            }

            sync.markAsProcessed(projectId);
        }
    }

    public void registerProcessedListener(Consumer<QueueAction> listener) {
        processedListeners.add(listener);
    }

    public boolean isProcessing() {
        return sync.isProcessing();
    }

    /**
     * don't forget about synchronized (sync.getLockForProject(projectId))
     */
    private LinkedHashSet<QueueAction> getQueueForProject(ObjectId projectId) {
        LinkedHashSet<QueueAction> answer = queues.get(projectId);

        if (answer == null) {
            answer = new LinkedHashSet<>();
            queues.put(projectId, answer);
        }

        return answer;
    }


    private <T> T getFirst(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
