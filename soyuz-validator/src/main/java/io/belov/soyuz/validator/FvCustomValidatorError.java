package io.belov.soyuz.validator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * Created by fbelov on 07.06.16.
 */
@EqualsAndHashCode
@Getter
public class FvCustomValidatorError<V> {
    private String code;
    @Nullable
    private V value;
    @Nullable
    private String property;
    @Nullable
    private Object[] args;

    public FvCustomValidatorError(String code) {
        this(code, null);
    }

    public FvCustomValidatorError(String code, V value) {
        this(null, code, value);
    }

    public FvCustomValidatorError(String property, String code, V value) {
        this(property, code, value, null);
    }

    public FvCustomValidatorError(String property, String code, V value, Object[] args) {
        this.property = property;
        this.code = code;
        this.value = value;
        this.args = args;
    }

    public boolean hasProperty() {
        return property != null;
    }

    public static <V> FvCustomValidatorError<V> from(FluentValidator.Error<V> error) {
        return new FvCustomValidatorError<>(error.getProperty(), error.getCode(), error.getValue(), error.getArgs());
    }
}
