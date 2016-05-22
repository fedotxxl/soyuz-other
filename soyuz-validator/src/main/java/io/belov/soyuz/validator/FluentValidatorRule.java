package io.belov.soyuz.validator;

import java.util.function.Function;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<V> {

    <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value);

    static interface Base {
        class Eq<V> implements FluentValidatorRule<V> {

            private V value;

            public Eq(V value) {
                this.value = value;
            }

            @Override
            public <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value) {
                return null;
            }
        }

        class EqFunction<V> implements FluentValidatorRule<V> {

            private Function<V, Boolean> eqFunction;

            public EqFunction(Function<V, Boolean> eqFunction) {
                this.eqFunction = eqFunction;
            }

            @Override
            public <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value) {
                return null;
            }
        }

        class NotEq<V> implements FluentValidatorRule<V> {

            private V value;

            public NotEq(V value) {
                this.value = value;
            }

            @Override
            public <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value) {
                return null;
            }
        }

        class NotEqFunction<V> implements FluentValidatorRule<V> {

            private Function<V, Boolean> notEqFunction;

            public NotEqFunction(Function<V, Boolean> notEqFunction) {
                this.notEqFunction = notEqFunction;
            }

            @Override
            public <R, P> FluentValidator.Error validate(R rootObject, P parentObject, V value) {
                return null;
            }
        }
     }
}
