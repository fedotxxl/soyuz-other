package io.belov.soyuz.spring.jdbc;

import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by fbelov on 27.11.15.
 */
abstract public class AbstractJdbcDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    protected Map queryForMapOrNull(String sql, Map<String, ?> params) {
        return Iterables.getFirst(namedJdbcTemplate.queryForList(sql, params), null);
    }

    protected long queryForLongOr(String sql, Map<String, ?> params, long or) {
        try {
            return namedJdbcTemplate.queryForLong(sql, params);
        } catch (EmptyResultDataAccessException e) {
            return or;
        }
    }

    protected Long queryForLongOrNull(String sql, Map<String, ?> params) {
        try {
            return namedJdbcTemplate.queryForLong(sql, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    protected int queryForIntOr(String sql, Map<String, ?> params, int or) {
        try {
            return namedJdbcTemplate.queryForInt(sql, params);
        } catch (EmptyResultDataAccessException e) {
            return or;
        }
    }

    protected Integer queryForIntOrNull(String sql, Map<String, ?> params) {
        try {
            return namedJdbcTemplate.queryForInt(sql, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    protected <T> T queryOrNull(Callable<T> query) {
        try {
            return query.call();
        } catch (Exception e) {
            return null;
        }
    }
}
