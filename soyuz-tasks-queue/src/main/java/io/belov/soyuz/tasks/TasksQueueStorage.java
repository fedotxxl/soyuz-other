package io.belov.soyuz.tasks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by fbelov on 10.03.16.
 */
public interface TasksQueueStorage {

    List<Task> findAllToProcess(@Nullable String type);
    List<Integer> restartTasksMarkedAsInProcess(@Nullable String type);
    void markToRepeatNow(int taskId);
    void markAsQueuedAndSetStatus(int taskId, Task.Status status);

}
