package io.thedocs.soyuz.db.jooq.crud;

import io.thedocs.soyuz.to;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Добавляет операции чтения в сервис
 */
public interface CrudReadServiceI<T extends CrudBeanI<I>, I, D extends CrudDaoI<T, I, LR>, LR extends JooqListRequestI> {

    interface Int<T extends CrudBeanI.Int, D extends CrudDaoI<T, Integer, LR>, LR extends JooqListRequestI> extends CrudReadServiceI<T, Integer, D, LR> {
    }

    D getDao();

    default ExecutorService getPool() {
        return CrudUtils.getDefaultPool();
    };

//    default FluentValidator<T> getValidator() {
//        return null;
//    }

    default T get(I id) {
        D dao = getDao();

        return postProcessLoadedData(dao.get(id));
    }

    default List<T> list() {
        D dao = getDao();

        return postProcessLoadedData(dao.list());
    }

    default List<T> list(Collection<I> ids) {
        D dao = getDao();

        return postProcessLoadedData(dao.list(ids));
    }

    default CollectionEntity<T> listWithTotal(LR request) {
        return listWithTotal(request, null);
    }

    default CollectionEntity<T> listWithTotal(LR request, @Nullable Function<T, T> transformer) {
        try {
            Integer total;
            Function<T, T> t = (transformer == null) ? (i) -> i : transformer;
            D dao = getDao();
            ExecutorService pool = getPool();

            Future<List<T>> itemsFuture = pool.submit(() -> {
                if (request.isShouldJoinToManyData()) {
                    return to.list(postProcessLoadedData(dao.list(request), request), t);
                } else {
                    return to.list(dao.list(request), t);
                }
            });

            if (request.isSkipTotal()) {
                total = -1;
            } else if (request.isNoOffsetsAndLimits()) {
                total = itemsFuture.get().size();
            } else {
                total = pool.submit(() -> dao.count(request)).get();
            }

            return new CollectionEntity<>(itemsFuture.get(), total);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default T postProcessLoadedData(T item) {
        return item;
    }

    default List<T> postProcessLoadedData(List<T> items) {
        return postProcessLoadedData(items, null);
    }

    default List<T> postProcessLoadedData(List<T> items, @Nullable LR request) {
        return items;
    }
}

