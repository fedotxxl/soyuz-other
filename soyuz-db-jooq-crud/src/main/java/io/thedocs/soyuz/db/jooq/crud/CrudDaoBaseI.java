package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Field;
import org.jooq.Record;

/**
 * JOOQ Crud - базовый класс работы с БД
 */
public interface CrudDaoBaseI<T, R extends Record> {

    CrudDaoI.JooqEntryData<T, R> getJooqEntryData();

    default Field<Integer> getIdField() {
        return getJooqEntryData().getTable().field("id", Integer.class);
    }

}
