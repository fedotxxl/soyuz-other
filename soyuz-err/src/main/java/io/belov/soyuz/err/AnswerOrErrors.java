package io.belov.soyuz.err;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created by fbelov on 31.05.16.
 */
@EqualsAndHashCode
@ToString
public class AnswerOrErrors<T> {

    private T answer;
    private Errors errors;

    private AnswerOrErrors(T answer) {
        this.answer = answer;
    }

    private AnswerOrErrors(Errors errors) {
        this.errors = errors;
    }

    private AnswerOrErrors(T answer, Errors errors) {
        this.answer = answer;
        this.errors = errors;
    }

    public AnswerOrErrors<T> ifOk(Consumer<AnswerOrErrors<T>> consumer) {
        if (consumer != null && isOk()) {
            consumer.accept(this);
        }

        return this;
    }

    public AnswerOrErrors<T> ifHasErrors(Consumer<AnswerOrErrors<T>> consumer) {
        if (consumer != null && hasErrors()) {
            consumer.accept(this);
        }

        return this;
    }

    public boolean isOk() {
        return !hasErrors();
    }

    public boolean hasErrors() {
        return errors != null && errors.hasErrors();
    }

    @Nullable
    public T getAnswer() {
        return answer;
    }

    @Nullable
    public Errors getErrors() {
        return errors;
    }

    public static <T> AnswerOrErrors<T> ok() {
        return new AnswerOrErrors<>((T) null);
    }

    public static <T> AnswerOrErrors<T> ok(T object) {
        return new AnswerOrErrors<>(object);
    }

    public static <T> AnswerOrErrors<T> failure(T object, Errors errors) {
        return new AnswerOrErrors<>(object, errors);
    }

    public static <T> AnswerOrErrors<T> failure(T object, Err... errors) {
        return new AnswerOrErrors<>(object, Errors.reject(errors));
    }

    public static <T> AnswerOrErrors<T> failure(Errors errors) {
        return new AnswerOrErrors<>(errors);
    }

    public static <T> AnswerOrErrors<T> failure(Err... errors) {
        return new AnswerOrErrors<>(Errors.reject(errors));
    }
}
