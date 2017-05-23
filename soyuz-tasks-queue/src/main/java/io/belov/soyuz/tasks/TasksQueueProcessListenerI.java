package io.belov.soyuz.tasks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by fbelov on 09.02.16.
 */
public interface TasksQueueProcessListenerI<T> {

    void on(Task task, T executionContext, AtomicReference<TasksQueueProcessorI.Result> result);
    void onException(Task task, T executionContext, Throwable e);

    interface Start {
        void onStart(Task task);
    }

    interface Finally<T> {
        void onFinally(Task task, T executionContext);
    }

    interface AfterTransaction<T> {
        void onAfterTransaction(Task task, T executionContext, AtomicReference<TasksQueueProcessorI.Result> result);
    }
}
