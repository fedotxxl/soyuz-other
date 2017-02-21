package io.belov.soyuz.tasks;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 18.03.16.
 */
public class TasksQueueToProcessSorterByPriority implements TasksQueueToProcessSorterI {
    private static final Comparator<Task> comparator = new TasksComparator();

    @Override
    public List<Task> sort(List<Task> tasks) {
        return tasks.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static class TasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task b, Task a) {
            int sortDifference = getSortValue(a) - getSortValue(b);

            if (sortDifference != 0) {
                return sortDifference;
            } else {
                if (a.hasBeenQueued()) {
                    return b.getQueuedOn().compareTo(a.getQueuedOn());
                } else {
                    return b.getPostedOn().compareTo(a.getPostedOn());
                }
            }
        }

        private int getSortValue(Task task) {
            int answer = task.getPriority() * 100;

            if (!task.hasBeenQueued()) {
                answer += 10;
            }

            return answer;
        }
    }
}
