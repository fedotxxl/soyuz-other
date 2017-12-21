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
public interface CrudReadDaoI<T, I, LR extends JooqListRequestI> extends CrudDaoBaseI<T, I> {

    interface Int<T, LR extends JooqListRequestI> extends CrudReadDaoI<T, Integer, LR> {
    }

    JooqEntryData<T> getJooqEntryData();

    Collection<Condition> getListConditions(LR request);

    default boolean has(I id) {
        JooqEntryData<T> data = getJooqEntryData();

        return data.dsl.selectCount().from(data.table).where(getIdField().eq(id)).fetchOne(0, int.class) > 0;
    }

    default T get(I id) {
        JooqEntryData<T> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).where(getIdField().eq(id)).fetchOne(data.mapper);
    }

    default List<T> list() {
        JooqEntryData<T> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).fetch(data.mapper);
    }

    default List<T> list(Collection<I> ids) {
        JooqEntryData<T> data = getJooqEntryData();

        return data.dsl.selectFrom(data.table).where(getIdField().in(ids)).fetch(data.mapper);
    }

    default List<T> list(LR request) {
        JooqEntryData<T> data = getJooqEntryData();
        JooqListRequestI.JooqParams params = request.getJooq();

        SelectSeekStepN<? extends Record> step = data.dsl.selectFrom(data.table)
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
        JooqEntryData<T> data = getJooqEntryData();

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
    class JooqEntryData<T> {
        private DSLContext dsl;
        private Table<? extends Record> table;
        private RecordMapper<Record, T> mapper;
        private JoinToManyDataMapper<T> joinToManyDataMapper;

        public JooqEntryData(DSLContext dsl, Table<? extends Record> table, RecordMapper<Record, T> mapper) {
            this.dsl = dsl;
            this.table = table;
            this.mapper = mapper;
        }

        public boolean hasJoinToManyDataMapper() {
            return joinToManyDataMapper != null;
        }
    }
}
