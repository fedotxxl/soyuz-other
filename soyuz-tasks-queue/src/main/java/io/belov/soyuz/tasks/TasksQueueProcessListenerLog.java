package io.belov.soyuz.tasks;

import com.google.common.collect.ImmutableMap;
import io.belov.soyuz.log.LoggerEvents;

import java.util.Map;

/**
 * Created by fbelov on 10.02.16.
 */
public class TasksQueueProcessListenerLog implements TasksQueueProcessListenerI<Object> {

    private static final LoggerEvents loge = LoggerEvents.getInstance(TasksQueueProcessListenerLog.class);

    @Override
    public void on(Task task, Object executionContext, TasksQueueProcessorI.Result result) {
        Map data = ImmutableMap.of("t", task.getId(), "r", result);
        String event = "tq.done";

        if (result == TasksQueueProcessorI.Result.SUCCESS) {
            loge.debug(event, data);
        } else if (result == TasksQueueProcessorI.Result.EXCEPTION) {
            loge.warn(event, data);
        } else {
            loge.info(event, data);
        }
    }

    @Override
    public void onException(Task task, Object executionContext, Throwable e) {
        loge.error("tq.done.exception", ImmutableMap.of("t", task.getId()), e);
    }

}
