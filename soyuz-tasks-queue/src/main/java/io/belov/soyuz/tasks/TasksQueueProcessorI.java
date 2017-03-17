package io.belov.soyuz.tasks;

/**
 * Created by fbelov on 09.02.16.
 */
public interface TasksQueueProcessorI<T> {

    Result process(Task task, T executionContext);

    enum Result {
        SUCCESS, FAILURE, SKIP, REPEAT, REPEAT_NOW, EXCEPTION;
    }

}
