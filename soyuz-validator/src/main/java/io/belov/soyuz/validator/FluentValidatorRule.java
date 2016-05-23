package io.belov.soyuz.validator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Function;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<V> {

    <R, P> Error validate(R rootObject, V value);

    abstract class AbstractRule<V> implements FluentValidatorRule<V> {

        public <R, P> Error validate(R rootObject, V value) {
            if (!isValid(rootObject, value)) {
                return new Error(getCode(), value);
            } else {
                return null;
            }
        }

        protected abstract String getCode();

        protected abstract <R, P> boolean isValid(R rootObject, V value);
    }


    static interface Base {

        class Eq<V> extends AbstractRule<V> {

            private V value;

            public Eq(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected <R, P> boolean isValid(R rootObject, V value) {
                return value != null && value.equals(this.value);
            }
        }

        class EqFunction<V> extends AbstractRule<V> {

            private Function<V, Boolean> eqFunction;

            public EqFunction(Function<V, Boolean> eqFunction) {
                this.eqFunction = eqFunction;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected <R, P> boolean isValid(R rootObject, V value) {
                return eqFunction.apply(value);
            }
        }

        class NotEq<V> implements FluentValidatorRule<V> {

            private V value;

            public NotEq(V value) {
                this.value = value;
            }

            @Override
            public <R, P> Error validate(R rootObject, V value) {
                return null;
            }
        }

        class NotEqFunction<V> implements FluentValidatorRule<V> {

            private Function<V, Boolean> notEqFunction;

            public NotEqFunction(Function<V, Boolean> notEqFunction) {
                this.notEqFunction = notEqFunction;
            }

            @Override
            public <R, P> Error validate(R rootObject, V value) {
                return null;
            }
        }
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    class Error<V> {
        private String code;
        private V value;

        public Error(String code, V value) {
            this.code = code;
            this.value = value;
        }
    }
}
