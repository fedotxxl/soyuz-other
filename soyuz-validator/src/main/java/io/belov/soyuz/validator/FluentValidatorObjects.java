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
            data.addRule(new FluentValidatorRule.Base.Eq<>(value));

            return _this();
        }

        public BuilderClass eq(Function<V, Boolean> eqFunction) {
            data.addRule(new FluentValidatorRule.Base.EqFunction<>(eqFunction));

            return _this();
        }

        public BuilderClass notEq(V value) {
            data.addRule(new FluentValidatorRule.Base.NotEq<>(value));

            return _this();
        }

        public BuilderClass notEq(Function<V, Boolean> notEqFunction) {
            data.addRule(new FluentValidatorRule.Base.NotEqFunction<>(notEqFunction));

            return _this();
        }

        public BuilderClass when(BiFunction<P, V, Boolean> when) {
            data.addWhen(when);

            return _this();
        }

        public BuilderClass when(Function<P, Boolean> when) {
            data.addWhen(when);

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

        protected BuilderClass _this() {
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
    public static interface FluentValidatorValidationData<V> {

        <R, P> FluentValidatorRule.Error validate(R rootObject, V value);

    }

    @Getter
    @Setter
    public static class BaseData<V> implements FluentValidatorValidationData<V> {

        private List<FluentValidatorRule<V>> rules = new ArrayList<>();

//        private V eq;
//        private Function<V, Boolean> eqFunction;
//        private V notEq;
//        private Function<V, Boolean> notEqFunction;
        private List<BiFunction> when = new ArrayList<>();
        private BiFunction unless;
        private FluentValidator.Data<V> validator;
        private final List<CustomValidator> customValidators = new ArrayList<>();
        private String message;

        public void addWhen(BiFunction<?, V, Boolean> when) {
            this.when.add(when);
        }

        public <P> void addWhen(Function<P, Boolean> when) {
            this.when.add((p, v) -> when.apply((P) p));
        }

        public void addRule(FluentValidatorRule<V> rule) {
            rules.add(rule);
        }

        public void addCustom(CustomValidator FluentValidatorCustom) {
            customValidators.add(FluentValidatorCustom);
        }

        @Override
        public <R, P> FluentValidatorRule.Error validate(R rootObject, V value) {
            for (BiFunction<R, V, Boolean> whenItem : when) {
                if (!whenItem.apply(rootObject, value)) {
                    return null;
                }
            }

            //todo unless

            for (FluentValidatorRule<V> rule : rules) {
                FluentValidatorRule.Error error = rule.validate(rootObject, value);

                if (error != null) {
                    return error;
                }
            }

            return null;
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

    public static class IntData extends BaseData<Integer> {
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
    public static class StringData extends ObjectData<String> {
        private boolean url;
        private boolean mail;
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
