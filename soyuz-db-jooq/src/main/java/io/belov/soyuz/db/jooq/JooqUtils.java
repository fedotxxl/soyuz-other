package io.belov.soyuz.db.jooq;

import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fbelov on 11.03.16.
 */
public class JooqUtils {

    public static Query map(Query query, Map<String, Object> params) {
        for (Map.Entry<String, Object> e : params.entrySet()) {
            query = query.bind(e.getKey(), e.getValue());
        }

        return query;
    }

    public static Field[] fields(Table<?> table, Map<Table<?>, String> tablesWithPrefixes) {
        return fields(to.list(table.fields()), tablesWithPrefixes);
    }

    public static Field[] fields(Field field, Map<Table<?>, String> tablesWithPrefixes) {
        return fields(to.list(field), tablesWithPrefixes);
    }

    public static Field[] fields(List<Field> fields, Map<Table<?>, String> tablesWithPrefixes) {
        for (Map.Entry<Table<?>, String> e : tablesWithPrefixes.entrySet()) {
            Collections.addAll(fields, fields(e.getKey(), e.getValue()));
        }

        return to.arr(fields, Field.class);
    }

    public static Field<?>[] fields(Table<?> table, String prefix) {
        return to.stream(table.fields()).map(f -> field(f, prefix)).toArray(Field<?>[]::new);
    }

    public static <V> Field<V> field(Field<V> field, String prefix) {
        if (is.t(prefix)) {
            return field.as(prefix + "_" + field.getName());
        } else {
            return field;
        }
    }

    public static Map<? extends Field<?>, ?> intoMap(Record r) {
        Map answer = new HashMap<>();

        for (Field field : r.fields()) {
            answer.put(field, r.getValue(field));
        }

        return answer;
    }

    public static Map<? extends Field<?>, ?> intoMapChanged(Record r) {
        Map answer = new HashMap<>();

        for (Field field : r.fields()) {
            if (r.changed(field)) {
                answer.put(field, r.getValue(field));
            }
        }

        return answer;
    }

}
