package io.belov.soyuz.validator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<R, V> {

    FluentValidator.Result validate(R rootObject, String property, V value);

    abstract class AbstractRule<R, V> implements FluentValidatorRule<R, V> {

        public FluentValidator.Result validate(R rootObject, String property, V value) {
            if (!isValid(rootObject, value)) {
                return FluentValidator.Result.failure(rootObject, property, getCode(), value, getErrorArgs());
            } else {
                return null;
            }
        }

        @Nullable
        public Object[] getErrorArgs() {
            return null;
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

    interface Int {
        class Min<R> extends AbstractRule<R, Integer> {
            private int min;

            public Min(int min) {
                this.min = min;
            }

            @Override
            protected String getCode() {
                return "min";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value > min;
            }

            @Override
            public Object[] getErrorArgs() {
                return new Object[]{min};
            }
        }

        class Max<R> extends AbstractRule<R, Integer> {
            private int max;

            public Max(int max) {
                this.max = max;
            }

            @Override
            protected String getCode() {
                return "max";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value < max;
            }

            @Override
            public Object[] getErrorArgs() {
                return new Object[]{max};
            }
        }
    }

    interface N {
        class Min<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V min;

            public Min(V min) {
                this.min = min;
            }

            @Override
            protected String getCode() {
                return "min";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(min) > 0;
            }
        }

        class Max<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V max;

            public Max(V max) {
                this.max = max;
            }

            @Override
            protected String getCode() {
                return "max";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(max) < 0;
            }
        }
    }

    interface D {
        class Before<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public Before(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "before";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) < 0;
            }
        }

        class After<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public After(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "after";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) > 0;
            }
        }

        class Between<R, V> extends AbstractRule<R, V> {
            private Supplier<V> afterSupplier;
            private Supplier<V> beforeSupplier;
            private Comparator<V> comparator;

            public Between(Supplier<V> afterSupplier, Supplier<V> beforeSupplier, Comparator<V> comparator) {
                this.afterSupplier = afterSupplier;
                this.beforeSupplier = beforeSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "between";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                if (value == null) {
                    return true;
                } else {
                    return comparator.compare(value, afterSupplier.get()) > 0 && comparator.compare(value, beforeSupplier.get()) < 0;
                }
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

    interface Coll {
        class NotEmpty<R, V> extends AbstractRule<R, Collection<V>> {

            @Override
            protected String getCode() {
                return "notEmpty";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value != null && !value.isEmpty();
            }
        }

        class Min<R, V> extends AbstractRule<R, Collection<V>> {
            private int min;

            public Min(int min) {
                this.min = min;
            }

            @Override
            protected String getCode() {
                return "min";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value != null && value.size() > min;
            }
        }

        class Max<R, V> extends AbstractRule<R, Collection<V>> {
            private int max;

            public Max(int max) {
                this.max = max;
            }

            @Override
            protected String getCode() {
                return "max";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value == null || value.size() < max;
            }
        }

        class ItemValidator<R, V> implements FluentValidatorRule<R, Collection<V>> {

            private FluentValidator<V> validator;

            public ItemValidator(FluentValidator<V> validator) {
                this.validator = validator;
            }

            @Override
            public FluentValidator.Result validate(R rootObject, String property, Collection<V> value) {
                if (value == null) {
                    return FluentValidator.Result.success();
                } else {
                    List<FluentValidator.Error> errors = new ArrayList<>();

                    for (V item : value) {
                        FluentValidator.Result result = validator.validate(item);

                        if (result.hasErrors()) {
                            errors.addAll(FluentValidatorObjects.ErrorUtils.addParentProperty(result.getErrors(), property));
                        }
                    }

                    if (errors.isEmpty()) {
                        return FluentValidator.Result.success();
                    } else {
                        return FluentValidator.Result.failure(rootObject, errors);
                    }
                }
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
                FvCustomValidatorResult result = null;

                if (customSimple != null) {
                    result = customSimple.validate(rootObject, value);
                } else if (customWithBuilder != null) {
                    result = customWithBuilder.validate(rootObject, value, new FluentValidatorBuilder<>());
                }

                if (result == null) {
                    return null;
                } else {
                    return toFvResult(result, rootObject, property, value);
                }
            }

            private FluentValidator.Result toFvResult(FvCustomValidatorResult result, R rootObject, String property, V value) {
                if (result.isOk()) {
                    return FluentValidator.Result.success(rootObject);
                } else {
                    List<FvCustomValidatorError> errorsSource = result.getErrors();
                    List<FluentValidator.Error> errors = new ArrayList<>(errorsSource.size());

                    for (FvCustomValidatorError e : errorsSource) {
                        errors.add(new FluentValidator.Error(
                                FluentValidatorObjects.PropertyUtils.mix(property, e.getProperty()),
                                e.getCode(),
                                (e.hasProperty()) ? e.getValue() : value,
                                e.getArgs()
                        ));
                    }

                    return FluentValidator.Result.failure(rootObject, errors);
                }
            }
        }

        class Validator<R, V> implements FluentValidatorRule<R, V> {

            private FluentValidator<V> validator;

            public Validator(FluentValidator<V> validator) {
                this.validator = validator;
            }

            @Override
            public FluentValidator.Result validate(R rootObject, String property, V value) {
                FluentValidator.Result result = validator.validate(value);

                if (result.isOk()) {
                    return result;
                } else {
                    return FluentValidator.Result.failure(rootObject, FluentValidatorObjects.ErrorUtils.addParentProperty(result.getErrors(), property));
                }
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
