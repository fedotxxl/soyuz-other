package io.thedocs.soyuz.db.jooq.performance;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.log.LoggerEvents;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created on 01.06.17.
 */
public class JooqPerformanceDbFlusher {

    private static final LoggerEvents loge = LoggerEvents.getInstance(JooqPerformanceDbFlusher.class);

    private JooqPerformanceCollector collector;
    private DSLContext dsl;

    private Table<Record> SQL_LOG;
    private Field<String> SQL_LOG_ID;
    private Field<String> SQL_LOG_QUERY;
    private Field<Integer> SQL_LOG_INVOCATIONS_COUNT;
    private Field<Long> SQL_LOG_TOTAL_DURATION_IN_MILLIS;
    private Field<Timestamp> SQL_LOG_LAST_UPDATED_AT;

    public JooqPerformanceDbFlusher(JooqPerformanceCollector collector, DSLContext dsl, int delayInSeconds) {
        this.collector = collector;
        this.dsl = dsl;
        this.SQL_LOG = DSL.table("SQL_LOG");
        this.SQL_LOG_ID = DSL.field("id", String.class);
        this.SQL_LOG_QUERY = DSL.field("query", String.class);
        this.SQL_LOG_INVOCATIONS_COUNT = DSL.field("invocations_count", Integer.class);
        this.SQL_LOG_TOTAL_DURATION_IN_MILLIS = DSL.field("total_duration_in_millis", Long.class);
        this.SQL_LOG_LAST_UPDATED_AT = DSL.field("last_updated_at", Timestamp.class);

        if (delayInSeconds > 0) {
            to.e.daemonForever("jooq-performance-db-flusher", delayInSeconds * 1000, this::flush, delayInSeconds * 1000).start();
        }
    }

    private void flush() {
        try {
            Collection<JooqPerformanceCollector.Item> items = collector.getCopyAndReset();

            if (is.t(items)) {
                if (loge.isTraceEnabled()) loge.trace("jooq.performance.flush.db", to.map("items", items.size()));

                Map<String, JooqPerformanceCollector.Item> byHashes = to.map(items, this::getHash);
                Collection<String> hashesFromDb = getHashesFromDb(byHashes.keySet());
                Collection<ItemWithHash> insert = to.list();
                Collection<ItemWithHash> update = to.list();

                byHashes.forEach((k, v) -> {
                    ItemWithHash itemWithKey = new ItemWithHash(k, v);

                    if (hashesFromDb.contains(k)) {
                        update.add(itemWithKey);
                    } else {
                        insert.add(itemWithKey);
                    }
                });

                doFlush(insert, update);
            }
        } catch (Exception e) {
            loge.error("jooq.performance.flush.db.e", e);
        }
    }

    private String getHash(JooqPerformanceCollector.Item item) {
        return Hashing.md5().hashString(item.getQuery(), Charsets.UTF_8).toString();
    }

    private Collection<String> getHashesFromDb(Collection<String> hashes) {
        return dsl.select(SQL_LOG_ID).from(SQL_LOG).where(SQL_LOG_ID.in(hashes)).fetch(SQL_LOG_ID);
    }

    private void doFlush(Collection<ItemWithHash> insert, Collection<ItemWithHash> update) {
        if (is.t(insert)) {
            dsl.batch(insert.stream().filter(this::isNotJooqPerformanceQueries).map(this::toInsertQuery).collect(Collectors.toList())).execute();
        }

        if (is.t(update)) {
            dsl.batch(update.stream().filter(this::isNotJooqPerformanceQueries).map(this::toUpdateQuery).collect(Collectors.toList())).execute();
        }
    }

    private boolean isNotJooqPerformanceQueries(ItemWithHash itemWithHash) {
        return !itemWithHash.getItem().getQuery().toLowerCase().contains("sql_log");
    }

    @AllArgsConstructor
    @Getter
    private static class ItemWithHash {
        private String hash;
        private JooqPerformanceCollector.Item item;
    }

    private Query toUpdateQuery(ItemWithHash itemWithHash) {
        JooqPerformanceCollector.Item item = itemWithHash.getItem();

        return dsl.update(SQL_LOG)
                .set(SQL_LOG_INVOCATIONS_COUNT, SQL_LOG_INVOCATIONS_COUNT.add(item.getInvocationsCount()))
                .set(SQL_LOG_TOTAL_DURATION_IN_MILLIS, SQL_LOG_TOTAL_DURATION_IN_MILLIS.add(item.getTotalDurationInMillis()))
                .set(SQL_LOG_LAST_UPDATED_AT, Timestamp.from(Instant.now()))
                .where(SQL_LOG_ID.eq(itemWithHash.getHash()));
    }

    private Query toInsertQuery(ItemWithHash itemWithHash) {
        JooqPerformanceCollector.Item item = itemWithHash.getItem();

        return dsl.insertInto(SQL_LOG)
                .set(SQL_LOG_ID, itemWithHash.getHash())
                .set(SQL_LOG_QUERY, stringLeft(item.getQuery(), 4096))
                .set(SQL_LOG_INVOCATIONS_COUNT, item.getInvocationsCount())
                .set(SQL_LOG_TOTAL_DURATION_IN_MILLIS, item.getTotalDurationInMillis())
                .set(SQL_LOG_LAST_UPDATED_AT, Timestamp.from(Instant.now()));
    }

    //org.apache.commons.lang3.StringUtils
    private static String stringLeft(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }
}
