package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Record;

import java.util.Collection;

/**
 * Добавляет операции чтения и записи
 */
public interface CrudDaoI<T, R extends Record, LR extends JooqListRequestI> extends CrudReadDaoI<T, R, LR> {

    T insertOrUpdate(T entry);

    default void delete(int id) {
        JooqEntryData<T, R> data = getJooqEntryData();

        data.getDsl().deleteFrom(data.getTable()).where(getIdField().eq(id)).execute();
    }

    default int deleteAllById(Collection<Integer> ids) {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.getDsl().deleteFrom(data.getTable()).where(getIdField().in(ids)).execute();
    }

}
