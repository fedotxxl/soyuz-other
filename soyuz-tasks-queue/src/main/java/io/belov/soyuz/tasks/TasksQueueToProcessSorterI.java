package io.belov.soyuz.tasks;

import java.util.List;

/**
 * Created by fbelov on 18.03.16.
 */
public interface TasksQueueToProcessSorterI {

    List<Task> sort(List<Task> tasks);

}
