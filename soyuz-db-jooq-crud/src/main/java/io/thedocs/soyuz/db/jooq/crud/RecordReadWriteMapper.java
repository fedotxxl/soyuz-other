package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;

import java.util.Map;

/**
 * Created on 25.07.17.
 */
public interface RecordReadWriteMapper<E> extends RecordMapper<Record, E> {

    Map<? extends Field<?>, ?> toFields(E entry);

    default Map<? extends Field<?>, ?> toFields(E entry, Field... excludeFields) {
        Map<? extends Field<?>, ?> answer = toFields(entry);

        if (excludeFields != null) {
            for (Field field : excludeFields) {
                answer.remove(field);
            }
        }

        return answer;
    }

}
