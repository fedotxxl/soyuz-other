package io.belov.soyuz.queue;

/**
 * Created by fbelov on 30.05.15.
 */
public interface QueuedActionProcessor<E extends QueueProcessResult> {

    E process(QueueAction queueAction);

}
