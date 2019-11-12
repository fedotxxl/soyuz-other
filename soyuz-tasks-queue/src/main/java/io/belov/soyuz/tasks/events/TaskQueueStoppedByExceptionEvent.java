package io.belov.soyuz.tasks.events;

import lombok.AllArgsConstructor;

/**
 * Created by fbelov on 14.10.16.
 */
@AllArgsConstructor
public class TaskQueueStoppedByExceptionEvent {
    private String queueName;
    private Throwable exception;

    public String getQueueName() {
        return queueName;
    }
}
