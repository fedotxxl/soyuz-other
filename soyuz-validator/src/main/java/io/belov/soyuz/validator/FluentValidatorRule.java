package io.belov.soyuz.validator;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<V> {

    <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value);

}
