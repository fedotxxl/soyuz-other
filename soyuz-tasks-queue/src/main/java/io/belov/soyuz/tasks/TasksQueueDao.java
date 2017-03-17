package io.belov.soyuz.tasks;

import io.belov.soyuz.json.JacksonUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

/**
 * Created by fbelov on 09.02.16.
 */
public class TasksQueueDao {

    private static final TaskRowMapper TASK_ROW_MAPPER = new TaskRowMapper();

    private JdbcTemplate jdbcTemplate;

    public TasksQueueDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Task get(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM task WHERE id = ?", new Object[]{id}, TASK_ROW_MAPPER);
    }

    public long insert(String type, int priority, Object context) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO task (type, priority, context) VALUES (?, ?, ?)", new String[]{"id"});

            ps.setString(1, type);
            ps.setInt(2, priority);
            ps.setString(3, JacksonUtils.toJson(context));

            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Task> findAllToProcess() {
        return findAllWithStatus(Task.Status.NEW);
    }

    public List<Task> findAllToProcessOfType(String type) {
        return findAllWithStatusAndType(Task.Status.NEW, type);
    }

    public List<Task> findAllInProgress() {
        return findAllWithStatus(Task.Status.IN_PROGRESS);
    }

    public List<Task> findAllInProgressWithType(String type) {
        return findAllWithStatusAndType(Task.Status.IN_PROGRESS, type);
    }

    private List<Task> findAllWithStatus(Task.Status status) {
        return jdbcTemplate.query("SELECT * FROM task WHERE status = ? ORDER BY posted_on ASC", new Object[]{status.getKey()}, TASK_ROW_MAPPER);
    }

    private List<Task> findAllWithStatusAndType(Task.Status status, String type) {
        return jdbcTemplate.query("SELECT * FROM task WHERE status = ? AND type = ? ORDER BY posted_on ASC", new Object[]{status.getKey(), type}, TASK_ROW_MAPPER);
    }

    public void markToRepeatNow(int taskId) {
        jdbcTemplate.update("UPDATE task SET queued_on = NULL, status = ?, status_on = NOW() WHERE id = ?", Task.Status.NEW.getKey(), taskId);
    }

    public void markAsQueuedAndSetStatus(int taskId, Task.Status status) {
        jdbcTemplate.update("UPDATE task SET queued_on = NOW(), status = ?, status_on = NOW() WHERE id = ?", status.getKey(), taskId);
    }

    public static class TaskRowMapper implements RowMapper<Task> {

        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Task(
                    rs.getInt("id"),
                    rs.getInt("priority"),
                    rs.getString("type"),
                    rs.getDate("posted_on"),
                    rs.getDate("queued_on"),
                    getStatus(rs),
                    rs.getString("context")
            );
        }

        private Task.Status getStatus(ResultSet rs) throws SQLException {
            String status = rs.getString("status");

            if (status == null) {
                return Task.Status.myValueOf(null);
            } else {
                return Task.Status.myValueOf(status.charAt(0));
            }
        }
    }
}
