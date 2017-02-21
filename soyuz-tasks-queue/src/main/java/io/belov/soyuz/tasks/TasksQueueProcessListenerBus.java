package io.belov.soyuz.tasks;

import com.google.common.eventbus.EventBus;

/**
 * Created by fbelov on 10.02.16.
 */
public class TasksQueueProcessListenerBus implements TasksQueueProcessListenerI<Object> {

    private EventBus bus;
    private String queueName;

    public TasksQueueProcessListenerBus(EventBus bus) {
        this(bus, null);
    }

    public TasksQueueProcessListenerBus(EventBus bus, String queueName) {
        this.queueName = queueName;
        this.bus = bus;
    }

    @Override
    public void on(Task task, Object executionContext, TasksQueueProcessorI.Result result) {
        bus.post(new TasksQueueResultEvent(queueName, task, executionContext, result));
    }

    @Override
    public void onException(Task task, Object executionContext, Throwable e) {
        bus.post(new TasksQueueResultEvent(queueName, task, executionContext, TasksQueueProcessorI.Result.EXCEPTION, e));
    }

}
