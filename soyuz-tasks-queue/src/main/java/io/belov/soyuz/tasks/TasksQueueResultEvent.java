package io.belov.soyuz.tasks;


import javax.annotation.Nullable;

/**
 * Created by fbelov on 14.03.16.
 */
public class TasksQueueResultEvent {

    @Nullable
    private String queueName;
    private Task task;
    private Object context;
    private TasksQueueProcessorI.Result result;
    private Throwable e;

    public TasksQueueResultEvent(String queueName, Task task, Object context, TasksQueueProcessorI.Result result) {
        this(queueName, task, context, result, null);
    }

    public TasksQueueResultEvent(@Nullable String queueName, Task task, Object context, TasksQueueProcessorI.Result result, @Nullable Throwable e) {
        this.queueName = queueName;
        this.task = task;
        this.result = result;
        this.context = context;
        this.e = e;
    }

    public boolean isQueue(String queueName) {
        return this.queueName != null && this.queueName.equals(queueName);
    }

    @Nullable
    public String getQueueName() {
        return queueName;
    }

    public Object getContext() {
        return context;
    }

    public <T> T getContext(Class<T> clazz) {
        try {
            return clazz.cast(context);
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public Throwable getE() {
        return e;
    }

    public TasksQueueProcessorI.Result getResult() {
        return result;
    }

    public Task getTask() {
        return task;
    }
}
