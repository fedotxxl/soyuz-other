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

    private Err() {
    }

    @Builder
    private Err(String field, String code, Code codeAsObject, String message, V value, Object[] args) {
        this.field = field;
        this.code = (codeAsObject != null) ? codeAsObject.getErrCode() : code;
        this.message = message;
        this.value = value;
        this.args = args;
    }
//
//    public static Err reject(Code code, Object... args) {
//        return reject(code.getErrCode(), args);
//    }
//
//    public static Err reject(String code, Object... args) {
//        return new Err<>(null, code, null, null, args);
//    }
//
//    public static <V> Err<V> reject(String field, Code code, V value) {
//        return reject(field, code.getErrCode(), value);
//    }
//
//    public static <V> Err<V> reject(String field, String code, V value) {
//        return new Err<>(field, code, null, value, null);
//    }
//
//    public static <V> Err<V> reject(String field, Code code, V value, Object... args) {
//        return reject(field, code.getErrCode(), value, args);
//    }
//
//    public static <V> Err<V> reject(String field, String code, V value, Object... args) {
//        return new Err<>(field, code, null, value, args);
//    }

    public static ErrBuilder code(String code) {
        return Err.builder().code(code);
    }

    public static ErrBuilder field(String field) {
        return Err.builder().field(field);
    }

    public interface Code {

        String getErrCode();

    }
}
