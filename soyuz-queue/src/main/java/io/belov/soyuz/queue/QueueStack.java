package io.belov.soyuz.queue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 29.05.15.
 */
public class QueueStack {

    private QueueSynchronizer sync;
    private QueueWithHandlers[] queueWithHandlers;
    private Queue[] queues;
    private Map<Queue, QueueWithHandlers> handlersByQueue;

    public QueueStack(QueueSynchronizer sync, QueueWithHandlers... queueWithHandlers) {
        this.sync = sync;
        this.queueWithHandlers = queueWithHandlers;
        this.queues = Arrays
                .stream(queueWithHandlers)
                .peek(this::setSynchronizer)
                .map(QueueWithHandlers::getQueue)
                .toArray(Queue[]::new);

        this.handlersByQueue = Arrays
                .stream(queueWithHandlers)
                .collect(Collectors.toMap(QueueWithHandlers::getQueue, Function.identity()));
    }

    public ActionWithQueue nextToProcess() {
        for (Queue queue : queues) {
            QueueAction action = queue.nextToProcess();

            if (action != null) {
                return new ActionWithQueue(action, queue);
            }
        }

        return null;
    }

    public boolean isProcessing() {
        for (Queue queue : queues) {
            if (queue.isProcessing()) return true;
        }

        return false;
    }

    public QueueWithHandlers[] getQueueWithHandlers() {
        return queueWithHandlers;
    }

    public QueueWithHandlers getHandlersForQueue(Queue queue) {
        return handlersByQueue.get(queue);
    }

    private void setSynchronizer(QueueWithHandlers queueWithHandlers) {
        queueWithHandlers.getQueue().setSynchronizer(sync);
    }
}
