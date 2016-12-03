package com.devadmin.spring.jdbc;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by fbelov on 27.11.15.
 */
public class JdbcUtils {

    public static final RowMapper<DateTime> dateTimeRowMapper = new RowMapper<DateTime>() {
        @Override
        public DateTime mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DateTime(rs.getTimestamp(1));
        }
    };

    public static Timestamp toTimestamp(ReadableInstant instant) {
        return (instant != null) ? new Timestamp(instant.getMillis()) : null;
    }

    public static Timestamp timestampNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static <T> T withNewTransaction(TransactionTemplate transactionTemplate, TransactionCallback<T> execute) {
        int propagationBehavior = -1;

        try {
            propagationBehavior = transactionTemplate.getPropagationBehavior();
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            return transactionTemplate.execute(execute);
        } finally {
            if (propagationBehavior != -1) {
                transactionTemplate.setPropagationBehavior(propagationBehavior);
            }
        }
    }

}
