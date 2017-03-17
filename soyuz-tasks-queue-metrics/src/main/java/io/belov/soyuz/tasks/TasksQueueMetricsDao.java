package io.belov.soyuz.tasks;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fbelov on 08.04.16.
 */
public class TasksQueueMetricsDao {

    private JdbcTemplate jdbcTemplate;

    public TasksQueueMetricsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TasksQueueMetricsData> getTasksQueueMetricsData() {
        return jdbcTemplate
                .queryForList("SELECT count(*) as count, type, status FROM task GROUP BY type, status")
                .stream()
                .map((m) -> new TasksQueueMetricsData(Task.Status.myValueOf(((String) m.get("status")).charAt(0)), (String) m.get("type"), (Long) m.get("count")))
                .filter((md) -> md.getStatus() != null)
                .collect(Collectors.toList());
    }

}
