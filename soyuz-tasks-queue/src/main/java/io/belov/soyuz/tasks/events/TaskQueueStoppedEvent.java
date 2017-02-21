package io.belov.soyuz.tasks.events;

/**
 * Created by fbelov on 14.10.16.
 */
public class TaskQueueStoppedEvent {
    private String queueName;

    public TaskQueueStoppedEvent(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
