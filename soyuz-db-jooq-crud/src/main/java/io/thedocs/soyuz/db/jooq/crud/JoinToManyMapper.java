package io.thedocs.soyuz.db.jooq.crud;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Маппер - преобразует множество joined записей в одну
 */
public interface JoinToManyMapper<E> {

    RecordMapper<Record, E> getMapper();

    E map(E entry, Result<? extends Record> result);

    default E mapSingle(Result<? extends Record> result) {
        Record record = result.stream().findFirst().orElse(null);

        if (record == null) {
            return null;
        } else {
            return map(getMapper().map(result.get(0)), result);
        }
    }

    default List<E> mapList(Field groupField, Result<? extends Record> result) {
        Map<Object, Result<? extends Record>> map = result.intoGroups(groupField);

        return map
                .values()
                .stream()
                .map(this::map)
                .filter(i -> i != null)
                .collect(Collectors.toList());
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
