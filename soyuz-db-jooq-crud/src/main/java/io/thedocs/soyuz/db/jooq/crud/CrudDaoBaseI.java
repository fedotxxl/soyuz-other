package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Field;
import org.jooq.Record;

/**
 * JOOQ Crud - базовый класс работы с БД
 */
public interface CrudDaoBaseI<T, I> {

    CrudDaoI.JooqEntryData<T> getJooqEntryData();

    default Field<I> getIdField() {
        return (Field<I>) getJooqEntryData().getTable().field("id");
    }

}
