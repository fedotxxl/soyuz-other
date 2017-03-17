package io.belov.soyuz.tasks;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 10.03.16.
 */
public class TasksQueueDbStorage implements TasksQueueStorage {

    private TasksQueueDao dao;

    public TasksQueueDbStorage(TasksQueueDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Task> findAllToProcess(@Nullable String type) {
        if (StringUtils.isEmpty(type)) {
            return dao.findAllToProcess();
        } else {
            return dao.findAllToProcessOfType(type);
        }
    }

    @Override
    public List<Integer> restartTasksMarkedAsInProcess(@Nullable String type) {
        return getTasksToRestart(type).stream()
                .map(Task::getId)
                .peek(dao::markToRepeatNow)
                .collect(Collectors.toList());
    }

    @Override
    public void markToRepeatNow(int taskId) {
        dao.markToRepeatNow(taskId);
    }

    @Override
    public void markAsQueuedAndSetStatus(int taskId, Task.Status status) {
        dao.markAsQueuedAndSetStatus(taskId, status);
    }

    private List<Task> getTasksToRestart(@Nullable String type) {
        if (StringUtils.isEmpty(type)) {
            return dao.findAllInProgress();
        } else {
            return dao.findAllInProgressWithType(type);
        }
    }

}
