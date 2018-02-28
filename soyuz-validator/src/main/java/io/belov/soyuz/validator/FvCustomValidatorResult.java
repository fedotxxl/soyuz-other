package io.belov.soyuz.validator;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Created by fbelov on 07.06.16.
 * todo mix with FluentValidator.Result
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FvCustomValidatorResult {
    private Errors errors;

    public boolean isOk() {
        return errors.isOk();
    }

    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public static FvCustomValidatorResult success() {
        return new FvCustomValidatorResult(Errors.ok());
    }

    public static FvCustomValidatorResult failure(String code) {
        return failure(code, null);
    }

    public static <V> FvCustomValidatorResult failure(String code, Map<String, Object> params) {
        return failure(null, code, null, params);
    }

    public static <V> FvCustomValidatorResult failure(String property, String code, V value) {
        return failure(property, code, value, null);
    }

    public static <V> FvCustomValidatorResult failure(String property, String code, V value, Map<String, Object> params) {
        return failure(Err.field(property).code(code).value(value).params(params).build());
    }

    public static FvCustomValidatorResult failure(Err error) {
        return new FvCustomValidatorResult(Errors.reject(error));
    }

    public static FvCustomValidatorResult failure(Errors errors) {
        return new FvCustomValidatorResult(errors);
    }

    public static FvCustomValidatorResult from(FluentValidator.Result result) {
        if (result.isOk()) {
            return success();
        } else {
            return failure(result.getErrors());
        }
    }
}
