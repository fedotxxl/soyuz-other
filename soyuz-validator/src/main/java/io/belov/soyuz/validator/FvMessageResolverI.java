package io.belov.soyuz.validator;

/**
 * Created by fbelov on 07.06.16.
 */
public interface FvMessageResolverI {

    <R, V> String getMessage(R rootObject, FluentValidator.Error<V> error);

}
