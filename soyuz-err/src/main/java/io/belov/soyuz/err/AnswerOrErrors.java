package io.belov.soyuz.err;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nullable;

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

    public static <K> AnswerOrErrors<K> ok() {
        return new AnswerOrErrors<>((K) null);
    }

    public static <K> AnswerOrErrors<K> ok(K object) {
        return new AnswerOrErrors<>(object);
    }

    public static <K> AnswerOrErrors<K> failure(K object, Errors errors) {
        return new AnswerOrErrors<>(object, errors);
    }

    public static <K> AnswerOrErrors<K> failure(K object, Err... errors) {
        return new AnswerOrErrors<>(object, Errors.reject(errors));
    }

    public static AnswerOrErrors failure(Errors errors) {
        return new AnswerOrErrors(errors);
    }

    public static AnswerOrErrors failure(Err... errors) {
        return new AnswerOrErrors(Errors.reject(errors));
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
}
