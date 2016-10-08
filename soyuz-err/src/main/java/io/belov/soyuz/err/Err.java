package io.belov.soyuz.err;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public boolean isGlobal() {
        return field == null;
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

    public static Err reject(String code, Object... args) {
        return new Err<>(null, code, null, null, args);
    }

    public static <V> Err<V> reject(String field, String code, V value) {
        return new Err<>(field, code, null, value, null);
    }

    public static <V> Err<V> reject(String field, String code, V value, Object... args) {
        return new Err<>(field, code, null, value, args);
    }

}
