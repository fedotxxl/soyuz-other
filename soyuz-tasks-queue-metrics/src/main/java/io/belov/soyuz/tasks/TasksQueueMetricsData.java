package io.belov.soyuz.tasks;

/**
 * Created by fbelov on 08.04.16.
 */
public class TasksQueueMetricsData {

    private Task.Status status;
    private String type;
    private long count;

    public TasksQueueMetricsData(Task.Status status, String type, long count) {
        this.status = status;
        this.type = type;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public Task.Status getStatus() {
        return status;
    }

    public String getStatusKey() {
        return status.getKey().toString();
    }

    public String getType() {
        return type;
    }

}
