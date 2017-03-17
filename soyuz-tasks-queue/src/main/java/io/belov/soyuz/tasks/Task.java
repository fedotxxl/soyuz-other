package io.belov.soyuz.tasks;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Created by fbelov on 09.02.16.
 */
public class Task {

    private int id;
    private int priority;
    private String type;
    private Status status;
    private Date postedOn;
    private Date queuedOn;
    private String context;

    public Task(int id, int priority, String type, Date postedOn, Date queuedOn, Status status, String context) {
        this.id = id;
        this.priority = priority;
        this.type = type;
        this.postedOn = postedOn;
        this.queuedOn = queuedOn;
        this.status = status;
        this.context = context;
    }

    public boolean hasBeenQueued() {
        return queuedOn != null;
    }

    //GETTERS
    public String getContext() {
        return context;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public Date getQueuedOn() {
        return queuedOn;
    }

    public Status getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public enum Status {

        NEW('n'), IN_PROGRESS('p'), SUCCESS('s'), FAILURE('f'), EXCEPTION('e');

        private Character key;

        Status(Character key) {
            this.key = key;
        }

        public Character getKey() {
            return key;
        }

        @Nullable
        public static Status myValueOf(Character key) {
            for (Status status : values()) {
                if (key.equals(status.getKey())) return status;
            }

            return null;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (priority != task.priority) return false;
        if (type != null ? !type.equals(task.type) : task.type != null) return false;
        if (status != task.status) return false;
        if (postedOn != null ? !postedOn.equals(task.postedOn) : task.postedOn != null) return false;
        if (queuedOn != null ? !queuedOn.equals(task.queuedOn) : task.queuedOn != null) return false;
        return !(context != null ? !context.equals(task.context) : task.context != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + priority;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (postedOn != null ? postedOn.hashCode() : 0);
        result = 31 * result + (queuedOn != null ? queuedOn.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }
}
