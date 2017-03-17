package io.belov.soyuz.tasks;

import java.util.Date;
import java.util.List;

/**
 * Created by fbelov on 10.02.16.
 */
public class TasksQueueSelectorQueuedOlderThan implements TasksQueueSelectorI {

    private int olderThanInMinutes;

    public TasksQueueSelectorQueuedOlderThan(int olderThanInMinutes) {
        this.olderThanInMinutes = olderThanInMinutes;
    }

    @Override
    public Task select(List<Task> tasks) {
        Date olderThan = new Date(System.currentTimeMillis() - (olderThanInMinutes * 60 * 1000));

        return tasks
                .stream()
                .filter(t -> filter(t, olderThan))
                .findFirst()
                .orElse(null);
    }

    private boolean filter(Task task, Date olderThan) {
        return !task.hasBeenQueued() || task.getQueuedOn().before(olderThan);
    }
}
