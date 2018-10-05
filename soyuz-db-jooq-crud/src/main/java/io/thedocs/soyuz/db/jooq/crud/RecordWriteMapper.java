package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.to;
import org.jooq.Field;

import java.util.Map;

/**
 * Created on 01.09.18.
 */
public interface RecordWriteMapper<E> {

    Map<? extends Field<?>, ?> toFields(E entry);

    default Map<? extends Field<?>, ?> toFieldsExcept(E entry, Field... excludeFields) {
        Map<? extends Field<?>, ?> answer = toFields(entry);

        if (excludeFields != null) {
            for (Field field : excludeFields) {
                answer.remove(field);
            }
        }

        return answer;
    }

    default Map<? extends Field<?>, ?> toFields(E entry, Field... fields) {
        Map answer = to.map();
        Map<? extends Field<?>, ?> data = toFields(entry);

        if (fields != null) {
            for (Field<?> field : fields) {
                answer.put(field, data.get(field));
            }
        }

        return (Map<? extends Field<?>, ?>) answer;
    }

}
