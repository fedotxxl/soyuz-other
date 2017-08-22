package io.thedocs.soyuz.db.jooq.crud;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Добавляет операции чтения с join'ом сторонних таблиц в сервис
 */
public interface CrudJoinServiceI<T extends CrudBeanI<?>, D extends CrudWriteJoinDaoI<T, LR>, LR extends JooqListRequestI> extends CrudServiceI<T, D, LR> {

    @Override
    default T postProcessLoadedData(T item) {
        return getDao().loadJoinToManyData(item);
    }

    @Override
    default List<T> postProcessLoadedData(List<T> items) {
        return getDao().loadJoinToManyData(items);
    }

    @Override
    default List<T> postProcessLoadedData(List<T> items, @Nullable LR request) {
        return getDao().loadJoinToManyData(items);
    }

}
