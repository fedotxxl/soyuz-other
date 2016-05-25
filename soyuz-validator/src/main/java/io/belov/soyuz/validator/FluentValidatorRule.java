package io.belov.soyuz.validator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.function.Function;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<R, V> {

    FluentValidator.Result validate(R rootObject, String property, V value);

    abstract class AbstractRule<R, V> implements FluentValidatorRule<R, V> {

        public FluentValidator.Result validate(R rootObject, String property, V value) {
            if (!isValid(rootObject, value)) {
                return FluentValidator.Result.failure(property, getCode(), value);
            } else {
                return null;
            }
        }

        protected abstract String getCode();

        protected abstract boolean isValid(R rootObject, V value);
    }

    interface Str {
        class NotEmpty<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "notEmpty";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && value.length() > 0;
            }
        }
    }

    interface Obj {
        class NotNull<R, V> extends AbstractRule<R, V> {
            @Override
            protected String getCode() {
                return "notNull";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value != null;
            }
        }
    }

    interface Base {

        class Eq<R, V> extends AbstractRule<R, V> {

            private V value;

            public Eq(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value != null && value.equals(this.value);
            }
        }

        class EqFunction<R, V> extends AbstractRule<R, V> {

            private Function<V, Boolean> eqFunction;

            public EqFunction(Function<V, Boolean> eqFunction) {
                this.eqFunction = eqFunction;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return eqFunction.apply(value);
            }
        }

        class NotEq<R, V> implements FluentValidatorRule<R, V> {

            private V value;

            public NotEq(V value) {
                this.value = value;
            }

            @Override
            public FluentValidator.Result validate(R rootObject, String property, V value) {
                return null;
            }
        }

        class NotEqFunction<R, V> implements FluentValidatorRule<R, V> {

            private Function<V, Boolean> notEqFunction;

            public NotEqFunction(Function<V, Boolean> notEqFunction) {
                this.notEqFunction = notEqFunction;
            }

            @Override
            public FluentValidator.Result validate(R rootObject, String property, V value) {
                return null;
            }
        }

        class Custom<R, V> implements FluentValidatorRule<R, V> {

            private FluentValidatorObjects.CustomValidator.Simple<R, V> customSimple;
            private FluentValidatorObjects.CustomValidator.WithBuilder<R, V> customWithBuilder;

            public Custom(FluentValidatorObjects.CustomValidator.Simple<R, V> customSimple) {
                this.customSimple = customSimple;
            }

            public Custom(FluentValidatorObjects.CustomValidator.WithBuilder<R, V> customWithBuilder) {
                this.customWithBuilder = customWithBuilder;
            }

            @Override
            public FluentValidator.Result validate(R rootObject, String property, V value) {
                if (customSimple != null) {
                    return customSimple.validate(rootObject, value);    
                } else if (customWithBuilder != null) {
                    return customWithBuilder.validate(rootObject, value, new FluentValidatorBuilder<>());    
                }
                
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
