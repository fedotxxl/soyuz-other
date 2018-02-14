package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Field;

import java.util.Map;

/**
 * Created on 25.07.17.
 */
public interface RecordWriteMapper<E> {

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
