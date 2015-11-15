package io.belov.soyuz.queue;

import com.google.common.base.Throwables;
import org.jongo.MongoCursor;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by fbelov on 01.06.15.
 */
public class QueueLoadProcessor {

    private volatile boolean isManaging = false;
    private Queue queue;
    private QueueLoader loader;
    private Collection<QueueAction> processedQueueActions = new HashSet<>();

    public QueueLoadProcessor(QueueWithHandlers queueWithHandlers) {
        this.loader = queueWithHandlers.getLoader();
        this.queue = queueWithHandlers.getQueue();
        this.queue.registerProcessedListener(processedQueueActions::add);
    }

    void startLoadingInNewThread() {
        Thread t = new Thread(this::startLoading);
        t.setDaemon(true);
        t.start();
    }

    private void startLoading() {
        try {
            isManaging = true;

            while (isManaging) {
                processedQueueActions.clear();

                boolean hasMoreSpace = true;
                MongoCursor<QueueAction> actions = loader.listActions();

                while (hasMoreSpace && actions != null && actions.hasNext()) {
                    QueueAction queueAction = actions.next();

                    if (!processedQueueActions.contains(queueAction)) { //action may be already processed
                        hasMoreSpace = queue.push(queueAction);
                    }
                }

                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Throwables.propagate(e);
        }
    }

    void stopLoading() {
        isManaging = false;
    }

}
