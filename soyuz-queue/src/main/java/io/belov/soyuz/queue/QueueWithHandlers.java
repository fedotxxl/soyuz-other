package io.belov.soyuz.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by fbelov on 01.06.15.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueueWithHandlers {

    private Queue queue;
    private QueueLoader loader;
    private QueuedActionProcessor processor;
    private QueueListener listener;

    public QueueWithHandlers(Queue queue, QueueDirector queueDirector) {
        this.queue = queue;
        this.loader = queueDirector;
        this.processor = queueDirector;
        this.listener = queueDirector;
    }
}
