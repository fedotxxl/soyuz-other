package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Record;
import org.jooq.Result;

/**
 * Маппер - преобразует множество joined записей в одну
 */
@FunctionalInterface
public interface JoinToManyDataMapper<E> {

    E map(E entry, Result<? extends Record> result);

}
