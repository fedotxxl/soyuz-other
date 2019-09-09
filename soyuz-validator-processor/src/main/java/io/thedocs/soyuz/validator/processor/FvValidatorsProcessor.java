package io.thedocs.soyuz.validator.processor;

import io.thedocs.soyuz.err.AnswerOrErrors;
import io.thedocs.soyuz.to;
import io.thedocs.soyuz.validator.Fv;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FvValidatorsProcessor {

    private Map<Class<? extends FvValidatableI>, FvValidateComponentI<? extends FvValidatableI>> validators;

    public FvValidatorsProcessor(List<FvValidateComponentI<? extends FvValidatableI>> validators) {
        this.validators = to.map(validators, i -> i.getRequestType());
    }

    public AnswerOrErrors<?> process(FvValidatableI request, Runnable command) {
        return process(null, request, command);
    }

    public AnswerOrErrors<?> process(@Nullable Class<?> validatorClass, FvValidatableI request, Runnable command) {
        return process(validatorClass, request, () -> {
            command.run();

            return Void.TYPE;
        });
    }

    /**
     *
     * @param request Реквест для прохождения валидации
     * @param command Команда, которая будет выполнена при успехе валидации
     * @return
     */
    @SneakyThrows
    public <V> AnswerOrErrors<V> process(FvValidatableI request, Callable<V> command) {
        return process(null, request, command);
    }

    @SneakyThrows
    public <V> AnswerOrErrors<V> process(@Nullable Class<?> validatorClass, FvValidatableI request, Callable<V> command) {
        FvValidateComponentI validator = validators.get(request.getClass());

        if (validator == null) {
            throw new RuntimeException("Validator for type [" + request.getClass().getSimpleName() + "] not supported! " + " Cannot process request");
        } else if (validatorClass != null && validator.getClass() != validatorClass) {
            throw new RuntimeException("Invalid validator class for type [" + request.getClass().getSimpleName() + "]. Expected: " + validatorClass + ", real: " + validator.getClass() +". Cannot process request");
        } else {
            Fv.Result answer = validator.get().validate(request);

            if (answer.hasErrors()) {
                return AnswerOrErrors.failure(answer.getErrors());
            } else {
                return AnswerOrErrors.ok(command.call());
            }
        }
    }

}
