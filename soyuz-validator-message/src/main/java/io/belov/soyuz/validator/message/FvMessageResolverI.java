package io.belov.soyuz.validator.message;

import io.belov.soyuz.validator.FluentValidator;

/**
 * Created by fbelov on 07.06.16.
 */
public interface FvMessageResolverI {

    <R, V> String getMessage(R rootObject, FluentValidator.Error<V> error);

}
