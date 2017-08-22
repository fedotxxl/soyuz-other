package io.thedocs.soyuz.db.jooq.crud;

import io.belov.soyuz.validator.FluentValidator;
import org.jooq.Record;

import javax.annotation.Nullable;

/**
 * Добавляет операции чтения и записи в сервис
 */
public interface CrudServiceI<T extends CrudBeanI<I>, I, D extends CrudDaoI<T, I, LR>, LR extends JooqListRequestI> extends CrudReadServiceI<T, I, D, LR>, CrudListenerI.PostUpdate<T>, CrudListenerI.PostDelete<T, I> {

    interface Int<T extends CrudBeanI.Int, D extends CrudDaoI<T, Integer, LR>, LR extends JooqListRequestI> extends CrudServiceI<T, Integer, D, LR> {
    }
    //    default FluentValidator<T> getValidator() {
//        return null;
//    }


//    default AnswerOrErrors<T> validate(T object) {
//        FluentValidator<T> validator = getValidator();
//
//        if (validator == null) {
//            return AnswerOrErrors.ok(object);
//        } else {
//            return toAnswerOrErrors(validator.validate(object));
//        }
//    }

//    default AnswerOrErrors<T> validateAndSave(T object) {
//        return validateAndSave(object, getValidator());
//    }
//
//    default AnswerOrErrors<T> validateAndSave(T object, @Nullable FluentValidator<T> validator) {
//        if (validator != null) {
//            FluentValidator.Result result = validator.validate(object);
//
//            if (result.hasErrors()) {
//                return toAnswerOrErrors(result);
//            }
//        }
//
//        return AnswerOrErrors.ok(save(object));
//    }

    default T save(T object) {
        FluentValidator<T> validator = getValidator();

        if (validator != null) {
            validator.validate(object).ifHasErrorsThrowAnException();
        }

        T answer = getDao().insertOrUpdate(object);

        postUpdate(answer);

        return answer;
    }

    default void delete(I id) {
        getDao().delete(id);
        postDelete(id);
    }

    @Nullable
    default FluentValidator<T> getValidator() {
        return null;
    }

}
