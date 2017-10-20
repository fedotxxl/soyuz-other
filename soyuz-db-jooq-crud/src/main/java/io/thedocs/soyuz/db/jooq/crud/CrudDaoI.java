package io.thedocs.soyuz.db.jooq.crud;

import java.util.Collection;

/**
 * Добавляет операции чтения и записи
 */
public interface CrudDaoI<T, I, LR extends JooqListRequestI> extends CrudReadDaoI<T, I, LR> {

    interface Int<T, LR extends JooqListRequestI> extends CrudDaoI<T, Integer, LR> {

    }

    T insertOrUpdate(T entry);

    default void delete(I id) {
        JooqEntryData<T> data = getJooqEntryData();

        data.getDsl().deleteFrom(data.getTable()).where(getIdField().eq(id)).execute();
    }

    default int deleteAllById(Collection<I> ids) {
        JooqEntryData<T> data = getJooqEntryData();

        return data.getDsl().deleteFrom(data.getTable()).where(getIdField().in(ids)).execute();
    }

}
