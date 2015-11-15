package io.belov.soyuz.queue;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;

/**
 * Created by fbelov on 30.05.15.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ActionWithQueue {

    private QueueAction action;
    private Queue queue;

    public void markAsProcessed() {
        queue.processed(action.getProjectId());
    }

    public String getQueueName() {
        return queue.getName();
    }

    public ObjectId getActionId() {
        return action.getId();
    }

}
