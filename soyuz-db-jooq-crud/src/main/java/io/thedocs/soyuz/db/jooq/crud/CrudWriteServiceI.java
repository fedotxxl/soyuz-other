package io.thedocs.soyuz.db.jooq.crud;

import io.belov.soyuz.validator.FluentValidator;

import javax.annotation.Nullable;

/**
 * Сервис - операции записи
 */
public interface CrudWriteServiceI<T extends CrudBeanI<I>, I, D extends CrudDaoI<T, I, LR>, LR extends JooqListRequestI> {

    interface Int<T extends CrudBeanI<Integer>, D extends CrudDaoI<T, Integer, LR>, LR extends JooqListRequestI> extends CrudWriteServiceI<T, Integer, D, LR> {
    }

    interface Long<T extends CrudBeanI<Long>, D extends CrudDaoI<T, Long, LR>, LR extends JooqListRequestI> extends CrudWriteServiceI<T, Long, D, LR> {
    }

    D getDao();

    default T save(T object) throws FluentValidator.ValidationException {
        FluentValidator<T> validator = getValidator();

        if (validator != null) {
            validator.validate(object).ifHasErrorsThrowAnException();
        }

        T answer = getDao().insertOrUpdate(object);

        postUpdate(answer);

        return answer;
    }

    default void delete(I id) {
        getDao().delete(getDao().getIdField().eq(id));

        postDelete(id);
    }

    default void postUpdate(T object) {
    }

    default void postDelete(I id) {
    }

    @Nullable
    default FluentValidator<T> getValidator() {
        return null;
    }

}
