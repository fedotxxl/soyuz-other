package io.belov.soyuz.err;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * Created by fbelov on 31.05.16.
 */
@Getter
@EqualsAndHashCode
@ToString
public class Err<V> {

    @Nullable
    private String field;
    private String code;
    @Nullable
    private String message;
    @Nullable
    private V value;
    @Nullable
    private Object[] args;

    @JsonIgnore
    public boolean isGlobalScope() {
        return field == null;
    }

    @JsonIgnore
    public boolean isFieldScope() {
        return field != null;
    }

    public static <K> ErrBuilder<K> builder() {
        return new ErrBuilder<>();
    }

    private Err() {
    }

    private Err(String field, String code, String message, V value, Object[] args) {
        this.field = field;
        this.code = code;
        this.message = message;
        this.value = value;
        this.args = args;
    }

    public static <V> ErrBuilder<V> code(Code code) {
        return Err.<V>builder().code(code);
    }

    public static <V> ErrBuilder<V> code(String code) {
        return Err.<V>builder().code(code);
    }

    public static <V> ErrBuilder<V> field(String field) {
        return Err.<V>builder().field(field);
    }

    public static <V> ErrBuilder<V> message(Code code) {
        return Err.<V>builder().code(code);
    }

    public interface Code {

        String getErrCode();

    }

    public static class ErrBuilder<V> {
        private String field;
        private String code;
        private String message;
        private V value;
        private Object[] args;

        public Err<V> build() {
            return new Err<>(field, code, message, value, args);
        }

        public ErrBuilder<V> field(String field) {
            this.field = field;

            return this;
        }

        public ErrBuilder<V> code(String code) {
            this.code = code;

            return this;
        }

        public ErrBuilder<V> code(Code code) {
            if (code != null) {
                this.code = code.getErrCode();
            }

            return this;
        }

        public ErrBuilder<V> message(String message) {
            this.message = message;

            return this;
        }

        public ErrBuilder<V> value(V value) {
            this.value = value;

            return this;
        }

        public ErrBuilder<V> args(Object... args) {
            this.args = args;

            return this;
        }
    }
}
