package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.to;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import java.util.List;
import java.util.Map;

/**
 * Маппер - преобразует множество joined записей в одну
 */
public interface JoinToManyMapper<E> {

    RecordMapper<Record, E> getMapper();

    E map(E entry, Result<? extends Record> result);

    default E mapSingle(Result<? extends Record> result) {
        return map(getMapper().map(result.get(0)), result);
    }

    default List<E> mapList(Field groupField, Result<? extends Record> result) {
        Map<Object, Result<? extends Record>> map = result.intoGroups(groupField);

        return to.list(map.values(), this::map);
    }

    default E map(Result<? extends Record> result) {
        if (result.size() == 0) {
            return null;
        } else {
            Record record = result.get(0);

            if (record == null) {
                return null;
            } else {
                E answer = getMapper().map(record);

                if (answer == null) {
                    return null;
                } else {
                    return map(answer, result);
                }
            }
        }

    }
}
