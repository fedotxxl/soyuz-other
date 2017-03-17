package io.belov.soyuz.tasks;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by fbelov on 09.02.16.
 */
public interface TasksQueueSelectorI {

    @Nullable
    Task select(List<Task> tasks);

}
