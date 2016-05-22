package io.belov.soyuz.validator;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 22.05.16.
 */
public class FluentValidatorObjects {

    public static class BaseBuilder<P, V, BuilderClass, DataClass extends BaseData<V>> {

        protected DataClass data;

        public BaseBuilder(DataClass data) {
            this.data = data;
        }

        public BuilderClass eq(V value) {
            data.setEq(value);

            return _this();
        }

        public BuilderClass eq(Function<V, Boolean> eqFunction) {
            data.setEqFunction(eqFunction);

            return _this();
        }

        public BuilderClass notEq(V value) {
            data.setNotEq(value);

            return _this();
        }

        public BuilderClass notEq(Function<V, Boolean> eqFunction) {
            data.setNotEqFunction(eqFunction);

            return _this();
        }

        public BuilderClass when(BiFunction<P, V, Boolean> when) {
            data.setWhen(when);

            return _this();
        }

        public BuilderClass unless(BiFunction<P, V, Boolean> unless) {
            data.setUnless(unless);

            return _this();
        }


        public BuilderClass validator(FluentValidator.Data<V> validator) {
            data.setValidator(validator);

            return _this();
        }

        public BuilderClass custom(CustomValidator.Simple<P, V> FluentValidatorCustom) {
            data.addCustom(FluentValidatorCustom);

            return _this();
        }

        public BuilderClass custom(CustomValidator.WithBuilder<P, V> customValidatorWithBuilder) {
            data.addCustom(customValidatorWithBuilder);

            return _this();
        }

        public BuilderClass message(String message) {
            data.setMessage(message);

            return _this();
        }

        private BuilderClass _this() {
            return (BuilderClass) this;
        }
    }

    /**
     * Created by fbelov on 22.05.16.
     */
    public static interface CustomValidator {

        public static interface Simple<P, V> extends CustomValidator {
            FluentValidator.Result validate(P object, V propertyValue);
        }

        public static interface WithBuilder<P, V> extends CustomValidator {
            FluentValidator.Result validate(P object, V propertyValue, FluentValidatorBuilder<V> fluentValidatorBuilder);
        }
    }

    /**
     * Created by fbelov on 22.05.16.
     */
    public static interface FluentValidatorValidationData {
    }

    @Getter
    @Setter
    public static class BaseData<V> implements FluentValidatorValidationData {
        private V eq;
        private Function<V, Boolean> eqFunction;
        private V notEq;
        private Function<V, Boolean> notEqFunction;
        private BiFunction when;
        private BiFunction unless;
        private FluentValidator.Data<V> validator;
        private final List<CustomValidator> customValidators = new ArrayList<>();
        private String message;

        public void addCustom(CustomValidator FluentValidatorCustom) {
            customValidators.add(FluentValidatorCustom);
        }
    }

    @Data
    public static class RootData<T> extends ObjectData<T> {
        private boolean failFast;
    }

    @Data
    public static class ObjectData<T> extends BaseData<T> {
        private boolean notNull;


        public ObjectData notNull() {
            notNull = true;
            return this;
        }
    }

    public static class IntData implements FluentValidatorValidationData {
        private Integer min;
        private Integer max;

        public IntData min(int min) {
            this.min = min;
            return this;
        }

        public IntData max(int max) {
            this.max = max;
            return this;
        }
    }

    @Setter
    @Getter
    public static class StringData extends ObjectData {
        private boolean url;
        private boolean mail;
        private boolean notEmpty;
        private Pattern matches;
    }

    public static class CollectionData extends ObjectData {
        private boolean notEmpty;
        private Integer min;
        private Integer max;

        public CollectionData notEmpty() {
            this.notEmpty = true;
            return this;
        }

        public CollectionData min(int min) {
            this.min = min;
            return this;
        }

        public CollectionData max(int max) {
            this.max = max;
            return this;
        }
    }
}
