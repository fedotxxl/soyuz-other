package io.belov.soyuz.tasks;

/**
 * Created by fbelov on 18.02.16.
 */
public interface TasksQueueContextCreatorI<T> {

    T createContext(Task task);

}
