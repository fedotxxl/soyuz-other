package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Record;
import org.jooq.RecordMapper;

/**
 * Created on 25.07.17.
 */
public interface RecordReadWriteMapper<E> extends RecordMapper<Record, E>, RecordWriteMapper<E> {

}
