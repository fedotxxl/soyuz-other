package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.*;

import java.util.Collection;
import java.util.List;

/**
 * Добавляет операции чтения
 */
public interface CrudReadDaoI<T, R extends Record, LR extends JooqListRequestI> extends CrudDaoBaseI<T, R> {

    JooqEntryData<T, R> getJooqEntryData();

    Collection<Condition> getListConditions(LR request);

    default Field<Integer> getIdField() {
        return getJooqEntryData().getTable().field("id", Integer.class);
    }

    default boolean has(int id) {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.dsl.selectCount().from(data.table).where(getIdField().eq(id)).fetchOne(0, int.class) > 0;
    }

    default T get(int id) {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).where(getIdField().eq(id)).fetchOne(data.mapper);
    }

    default List<T> list() {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).fetch(data.mapper);
    }

    default List<T> list(Collection<Integer> ids) {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).where(getIdField().in(ids)).fetch(data.mapper);
    }

    default List<T> list(LR request) {
        JooqEntryData<T, R> data = getJooqEntryData();
        JooqListRequestI.JooqParams params = request.getJooq();

        SelectSeekStepN<? extends R> step = data.dsl.selectFrom(data.table)
                .where(getListConditions(request))
                .orderBy(getSortFields(params.getSortFields()));

        if (params.hasOffsetAndLimit()) {
            return step
                    .limit(params.getLimit())
                    .offset(params.getOffset())
                    .fetch(data.getMapper());
        } else {
            return step
                    .fetch(data.getMapper());
        }
    }

    default int count(LR request) {
        JooqEntryData<T, R> data = getJooqEntryData();

        return data.dsl.selectCount()
                .from(data.table)
                .where(getListConditions(request))
                .fetchOne(0, int.class);
    }

    default List<SortField<?>> getSortFields(List<FieldWithOrder> fieldsWithOrder) {
        if (!is.t(fieldsWithOrder)) {
            return to.list();
        } else {
            return to.list(fieldsWithOrder, (o) -> {
                SortOrder order = (o.getOrder().equalsIgnoreCase("asc")) ? SortOrder.ASC : SortOrder.DESC;
                SortField<?> field = toJooqField(o.getField()).sort(order);

                if (order == SortOrder.DESC) {
                    field = field.nullsLast();
                }

                return field;
            });
        }
    }

    default Field<?> toJooqField(String field) {
        Field<?> answer = getJooqEntryData().getTable().field(field);

        if (answer == null) {
            throw new IllegalStateException("Unable to get field for " + field);
        } else {
            return answer;
        }
    }

    @AllArgsConstructor
    @Getter
    class JooqEntryData<T, R extends Record> {
        private DSLContext dsl;
        private Table<? extends R> table;
        private RecordMapper<R, T> mapper;
        private JoinToManyDataMapper<Record, T> joinToManyDataMapper;

        public JooqEntryData(DSLContext dsl, Table<? extends R> table, RecordMapper<R, T> mapper) {
            this.dsl = dsl;
            this.table = table;
            this.mapper = mapper;
        }

        public boolean hasJoinToManyDataMapper() {
            return joinToManyDataMapper != null;
        }
    }
}
