package io.belov.soyuz.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by fbelov on 28.05.15.
 */
@EqualsAndHashCode
@ToString
public class QueueAction {

    protected ObjectId _id;
    protected int tries = 0;
    protected Date lastTry;
    protected String type;

    @JsonProperty("pId")
    protected ObjectId projectId;

    public QueueAction() {
    }

    public QueueAction(ObjectId projectId) {
        this.projectId = projectId;
    }

    public QueueAction(ObjectId projectId, String type) {
        this.projectId = projectId;
        this.type = type;
    }

    public ObjectId getId() {
        return _id;
    }

    public ObjectId getProjectId() {
        return projectId;
    }

    public int getTries() {
        return tries;
    }

    public Date getLastTry() {
        return lastTry;
    }

    public String getType() {
        return type;
    }
}
