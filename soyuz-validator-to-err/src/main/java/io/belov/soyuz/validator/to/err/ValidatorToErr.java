package io.belov.soyuz.validator.to.err;

import io.belov.soyuz.err.AnswerOrErrors;
import io.belov.soyuz.err.Err;
import io.belov.soyuz.err.Errors;
import io.belov.soyuz.validator.FluentValidator;

import java.util.stream.Collectors;

/**
 * Created by fbelov on 27.06.16.
 */
public class ValidatorToErr {

    public static <T> AnswerOrErrors<T> toAnswerOrErrors(FluentValidator.Result<T> result) {
        if (result.isOk()) {
            return AnswerOrErrors.ok(result.getRootObject());
        } else {
            return AnswerOrErrors.failure(Errors.reject(result.getErrors().stream().map(ValidatorToErr::toErr).collect(Collectors.toList())));
        }
    }

    private static Err toErr(FluentValidator.Error error) {
        return Err.reject(error.getProperty(), error.getCode(), error.getValue(), error.getArgs());
    }

}