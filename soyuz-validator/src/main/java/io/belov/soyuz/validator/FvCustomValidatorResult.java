package io.belov.soyuz.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fbelov on 07.06.16.
 * todo mix with FluentValidator.Result
 */
@Getter
public class FvCustomValidatorResult {
    private List<FvCustomValidatorError> errors;

    private FvCustomValidatorResult(List<FvCustomValidatorError> errors) {
        this.errors = errors;
    }

    public boolean isOk() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static FvCustomValidatorResult success() {
        return new FvCustomValidatorResult(Collections.unmodifiableList(new ArrayList<>()));
    }

    public static FvCustomValidatorResult failure(String code) {
        return failure(code, null);
    }

    public static <V> FvCustomValidatorResult failure(String code, Object[] args) {
        return failure(null, code, null, args);
    }

    public static <V> FvCustomValidatorResult failure(String property, String code, V value) {
        return failure(property, code, value, null);
    }

    public static <V> FvCustomValidatorResult failure(String property, String code, V value, Object[] args) {
        return failure(new FvCustomValidatorError<>(property, code, value, args));
    }

    public static FvCustomValidatorResult failure(FvCustomValidatorError error) {
        List<FvCustomValidatorError> answer = new ArrayList<>();

        answer.add(error);

        return new FvCustomValidatorResult(answer);
    }

    public static FvCustomValidatorResult failure(List<FvCustomValidatorError> errors) {
        return new FvCustomValidatorResult(errors);
    }

    public static FvCustomValidatorResult from(FluentValidator.Result result) {
        if (result.isOk()) {
            return success();
        } else {
            List<FluentValidator.Error> errorsSource = result.getErrors();
            List<FvCustomValidatorError> errors = new ArrayList<>(errorsSource.size());

            for (FluentValidator.Error e : errorsSource) {
                errors.add(FvCustomValidatorError.from(e));
            }

            return failure(errors);
        }
    }
}
