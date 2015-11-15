package io.belov.soyuz.queue;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.belov.soyuz.log.MdcRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 30.05.15.
 */
public class QueueProcessor {

    private static final Logger log = LoggerFactory.getLogger(QueueProcessor.class);

    private volatile boolean isProcessing = false;

    private ExecutorService threadPool;
    private QueueStack queueStack;
    private List<QueueLoadProcessor> queueLoadProcessors;

    public QueueProcessor(int threadsCount, QueueStack queueStack) {
        this.threadPool = Executors.newFixedThreadPool(threadsCount, new ThreadFactoryBuilder().setNameFormat("qp-%d").build());
        this.queueStack = queueStack;
    }

    public void startProcessingInNewThread() {
        Thread t = new Thread(this::startProcessing);
        t.setDaemon(true);
        t.start();
    }

    public void stopProcessing() {
        queueLoadProcessors
                .stream()
                .forEach(QueueLoadProcessor::stopLoading);

        isProcessing = false;
    }


    private void startProcessing() {
        isProcessing = true;

        queueLoadProcessors = Arrays
                .stream(queueStack.getQueueWithHandlers())
                .map(QueueLoadProcessor::new)
                .collect(Collectors.toList());

        queueLoadProcessors
                .stream()
                .forEach(QueueLoadProcessor::startLoadingInNewThread);

        try {
            while (isProcessing) {
                ActionWithQueue actionWithQueue = queueStack.nextToProcess();

                if (actionWithQueue == null) {
                    Thread.sleep(queueStack.isProcessing() ? 15 : 100);
                } else {
                    threadPool.submit(new MdcRunnable("q", actionWithQueue.getQueueName(), () -> {
                        try {
                            processAction(actionWithQueue);
                        } finally {
                            actionWithQueue.markAsProcessed();
                        }
                    }));
                }
            }
        } catch (InterruptedException e) {
            Throwables.propagate(e);
        }
    }

    private void processAction(ActionWithQueue actionWithQueue) {
        QueueAction action = actionWithQueue.getAction();
        QueueWithHandlers handlers = queueStack.getHandlersForQueue(actionWithQueue.getQueue());
        QueueListener listener = handlers.getListener();

        try {
            listener.onStart(actionWithQueue);

            QueueProcessResult answer = handlers.getProcessor().process(action);

            if (answer.isSuccess()) {
                listener.onProcessed(actionWithQueue, answer);
            } else {
                listener.onFailure(actionWithQueue);
            }
        } catch (Throwable t) {
            log.error("Exception on processing action " + action, t);
            listener.onError(actionWithQueue);
        } finally {
            listener.onFinally(actionWithQueue);
        }
    }

}
