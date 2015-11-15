package io.belov.soyuz.queue;

/**
 * Created by fbelov on 27.06.15.
 */
public interface QueueDirector<E extends QueueProcessResult> extends QueuedActionProcessor<E>, QueueListener<E>, QueueLoader {
}
