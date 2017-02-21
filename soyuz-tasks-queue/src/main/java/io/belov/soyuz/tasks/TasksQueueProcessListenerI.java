package io.belov.soyuz.tasks;

/**
 * Created by fbelov on 09.02.16.
 */
public interface TasksQueueProcessListenerI<T> {

    void on(Task task, T executionContext, TasksQueueProcessorI.Result result);
    void onException(Task task, T executionContext, Throwable e);

    interface Finally<T> {
        void onFinally(Task task, T executionContext);
    }

    interface AfterTransaction<T> {
        void onAfterTransaction(Task task, T executionContext, TasksQueueProcessorI.Result result);
    }
}
