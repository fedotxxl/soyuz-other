package io.thedocs.soyuz.validator.processor;

import io.thedocs.soyuz.validator.Fv;

public interface FvValidateComponentI<T extends FvValidatableI> {

    Fv.Validator<T> get();

    Class<T> getRequestType();
}
