package io.thedocs.soyuz.err;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fbelov on 31.05.16.
 */
@EqualsAndHashCode
@ToString
@JsonSerialize(using = AnswerOrErrors.Serializer.class)
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

    public AnswerOrErrors<T> ifOk(Runnable runnable) {
        if (runnable != null && isOk()) {
            runnable.run();
        }

        return this;
    }

    public AnswerOrErrors<T> ifOk(Supplier<AnswerOrErrors<T>> supplier) {
        if (supplier != null && isOk()) {
            return supplier.get();
        } else {
            return this;
        }
    }

    public AnswerOrErrors<T> ifOk(Function<AnswerOrErrors<T>, AnswerOrErrors<T>> func) {
        if (func != null && isOk()) {
            return func.apply(this);
        } else {
            return this;
        }
    }

    public AnswerOrErrors<T> ifHasErrors(Consumer<AnswerOrErrors<T>> consumer) {
        if (consumer != null && hasErrors()) {
            consumer.accept(this);
        }

        return this;
    }

    public AnswerOrErrors<T> ifHasErrors(Runnable runnable) {
        if (runnable != null && hasErrors()) {
            runnable.run();
        }

        return this;
    }

    public AnswerOrErrors<T> ifHasErrors(Supplier<AnswerOrErrors<T>> supplier) {
        if (supplier != null && hasErrors()) {
            return supplier.get();
        } else {
            return this;
        }
    }

    public AnswerOrErrors<T> ifHasErrors(Function<AnswerOrErrors<T>, AnswerOrErrors<T>> func) {
        if (func != null && hasErrors()) {
            return func.apply(this);
        } else {
            return this;
        }
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
        if (object instanceof Err) {
            return new AnswerOrErrors<>(null, Errors.reject((Err) object));
        } else {
            return new AnswerOrErrors<>(object, Errors.reject(errors));
        }
    }

    public static <T> AnswerOrErrors<T> failure(Errors errors) {
        return new AnswerOrErrors<>(errors);
    }

    public static <T> AnswerOrErrors<T> failure(Err... errors) {
        return new AnswerOrErrors<>(Errors.reject(errors));
    }

    protected static class Serializer extends JsonSerializer<AnswerOrErrors> {

        @Override
        public void serialize(AnswerOrErrors value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            Map a = new HashMap();

            a.put("answer", value.getAnswer());
            a.put("ok", value.isOk());

            if (!value.hasErrors()) {
                a.put("errors", null);
            } else {
                a.put("errors", value.errors.get());
            }

            jgen.writeObject(a);
        }

    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Deserializer<T> {

        private T answer;
        private List<Err> errors;

        public AnswerOrErrors<T> toAnswerOrErrors() {
            if (errors != null && errors.size() > 0) {
                return new AnswerOrErrors<T>(answer, Errors.reject(errors));
            } else {
                return AnswerOrErrors.ok(answer);
            }
        }

    }
}
