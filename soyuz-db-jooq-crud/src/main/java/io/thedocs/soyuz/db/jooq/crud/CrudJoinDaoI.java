package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.to;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStepN;

import java.util.List;
import java.util.Map;

/**
 * Добавляет операции чтения с join'ом сторонних таблиц
 */
public interface CrudJoinDaoI<T extends CrudBeanI<I>, I, LR extends JooqListRequestI> extends CrudReadDaoI<T, I, LR> {

    interface Int<T extends CrudBeanI.Int, LR extends JooqListRequestI> extends CrudJoinDaoI<T, Integer, LR> {
    }

    default T get(I id) {
        JooqEntryData<T> data = getJooqEntryData();

        return getJoinToOneStep()
                .where(getIdField().eq(id))
                .fetchOne(data.getMapper());
    }

    default List<T> list() {
        JooqEntryData<T> data = getJooqEntryData();

        return getJoinToOneStep().fetch(data.getMapper());
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

    default SelectJoinStep<Record> getJoinToOneStep() {
        return getJooqEntryData().getDsl().select().from(getJooqEntryData().getTable());
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
}

