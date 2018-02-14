package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Добавляет операции чтения
 */
public interface CrudDaoI<T, I, LR extends JooqListRequestI> {

    interface Int<T, LR extends JooqListRequestI> extends CrudDaoI<T, Integer, LR> {
    }

    JooqEntryData<T> getJooqEntryData();

    Collection<Condition> getListConditions(LR request);

    //--write
    default T insertOrUpdate(T entry) {
        throw new UnsupportedOperationException("insertOrUpdate is not implemented");
    }

    default int delete(Condition condition) {
        throw new UnsupportedOperationException("delete is not implemented");
    }

    //--read
    default T get(I id) {
        return getJoinToOneStep().where(getIdField().eq(id)).fetchOne(getJooqEntryData().getMapper());
    }

    default boolean has(I id) {
        AggregateFunction<Integer> count = DSL.count();

        return getJoinToOneStep(to.list(count)).where(getIdField().eq(id)).fetchOne(count) > 0;
    }

    default int count(LR request) {
        AggregateFunction<Integer> count = DSL.count();

        return getJoinToOneStep(to.list(count)).where(getListConditions(request)).fetchOne(count);
    }

    default List<T> list(LR request) {
        JooqEntryData<T> data = getJooqEntryData();
        JooqListRequestI.JooqParams params = request.getJooq();

        SelectSeekStepN<Record> step = getJoinToOneStep()
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

    default List<T> list(Condition condition) {
        return getJoinToOneStep().where(condition).fetch(getJooqEntryData().getMapper());
    }

    default SelectJoinStep<Record> getJoinToOneStep() {
        return getJoinToOneStep(to.list());
    }

    default SelectJoinStep<Record> getJoinToOneStep(Collection<? extends SelectField<?>> fields) {
        return getJooqEntryData().getDsl().select(fields).from(getJooqEntryData().getTable());
    }

    default SelectJoinStep<Record> getJoinToManyStep() {
        return getJooqEntryData().getDsl().select().from(getJooqEntryData().getTable());
    }

    default T loadJoinToManyData(T entry) {
        if (entry == null) {
            return null;
        } else if (!getJooqEntryData().hasJoinToManyDataMapper()) {
            return entry;
        } else {
            return loadJoinToManyData(to.list(entry)).get(0);
        }
    }

    default List<T> loadJoinToManyData(List<T> entries) {
        JooqEntryData<T> data = getJooqEntryData();

        if (!data.hasJoinToManyDataMapper()) {
            return entries;
        } else {
            Map<I, T> entriesByIds = to.map(entries, CrudBeanI::getId);

            JoinToManyDataMapper<T> joinToManyDataMapper = data.getJoinToManyDataMapper();

            Map<I, Result<Record>> result = getJoinToManyStep().where(getIdField().in(entriesByIds.keySet())).fetchGroups(getIdField());

            return to.list(entries, (e) -> {
                Result<Record> record = result.get(e.getId());

                if (record == null) {
                    return e;
                } else {
                    return joinToManyDataMapper.map(e, record);
                }
            });
        }
    }

    default Field<I> getIdField() {
        return (Field<I>) getJooqEntryData().getTable().field("id");
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
