package io.thedocs.soyuz.err;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.Map;

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
    private Map<String, Object> params;

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

    private Err(String field, String code, String message, V value, Map<String, Object> params) {
        this.field = field;
        this.code = code;
        this.message = message;
        this.value = value;
        this.params = params;
    }

    public ErrBuilder<V> toBuilder() {
        return Err.<V>builder().field(field).code(code).message(message).value(value).params(params);
    }

    public boolean hasField() {
        return field != null;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean hasParams() {
        return params != null;
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
        private Map<String, Object> params;

        public Err<V> build() {
            return new Err<>(field, code, message, value, params);
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

        public ErrBuilder<V> params(Map<String, Object> params) {
            this.params = params;

            return this;
        }
    }
}
