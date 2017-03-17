package io.belov.soyuz.tasks;

import io.belov.soyuz.utils.to;
import io.prometheus.client.Gauge;

/**
 * Created by fbelov on 08.04.16.
 */
public class TasksQueueRegistry {

    private Gauge tasksByStatues;

    private TasksQueueMetricsDao dao;

    public TasksQueueRegistry(String prefix, int repeatEverySeconds, TasksQueueMetricsDao dao) {
        this.tasksByStatues = Gauge.build().name(prefix + "_tasks_by_statuses").labelNames("status", "type").help("Tasks by statuses").register();
        this.dao = dao;

        setUp(prefix, repeatEverySeconds);
    }

    private void setUp(String prefix, int repeatEverySeconds) {
        to.daemonForever(prefix + "_tasks_metrics_collector", repeatEverySeconds*1000, this::collectMetrics).start();
    }

    private void collectMetrics() {
        for (TasksQueueMetricsData data : dao.getTasksQueueMetricsData()) {
            tasksByStatues.labels(data.getStatusKey(), data.getType()).set(data.getCount());
        }
    }
}
