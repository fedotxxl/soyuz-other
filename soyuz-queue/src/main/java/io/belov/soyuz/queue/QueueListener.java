package io.belov.soyuz.queue;

/**
 * Created by fbelov on 30.05.15.
 */
public interface QueueListener<E extends QueueProcessResult> {

    void onStart(ActionWithQueue actionWithQueue);
    void onProcessed(ActionWithQueue actionWithQueue, E result);
    void onFailure(ActionWithQueue actionWithQueue);
    void onError(ActionWithQueue actionWithQueue);
    void onFinally(ActionWithQueue actionWithQueue);

}
