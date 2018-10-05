package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Record;
import org.jooq.RecordMapper;

/**
 * Created on 01.09.18.
 */
public interface RecordReadWriteMapper<E> extends RecordMapper<Record, E>, RecordWriteMapper<E> {

}
