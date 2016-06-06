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

    public static class BaseBuilder<R, V, BuilderClass, DataClass extends BaseData<R, V>> {

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

        public BuilderClass when(BiFunction<R, V, Boolean> when) {
            data.addWhen(when);

            return _this();
        }

        public BuilderClass when(Function<R, Boolean> when) {
            data.addWhen(when);

            return _this();
        }

        public BuilderClass unless(BiFunction<R, V, Boolean> unless) {
            data.setUnless(unless);

            return _this();
        }


        public BuilderClass validator(FluentValidator.Data<V> validator) {
            data.addRule(new FluentValidatorRule.Base.Validator<>(validator));

            return _this();
        }

        public BuilderClass custom(CustomValidator.Simple<R, V> fluentValidatorCustom) {
            data.addRule(new FluentValidatorRule.Base.Custom<>(fluentValidatorCustom));

            return _this();
        }

        public BuilderClass custom(CustomValidator.WithBuilder<R, V> customValidatorWithBuilder) {
            data.addRule(new FluentValidatorRule.Base.Custom<>(customValidatorWithBuilder));

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
    public static interface FluentValidatorValidationData<R, V> {

        FluentValidator.Result validate(R rootObject, String property, V value);

    }

    @Getter
    @Setter
    public static class BaseData<R, V> implements FluentValidatorValidationData<R, V> {

        private List<FluentValidatorRule<R, V>> rules = new ArrayList<>();

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

        public void addRule(FluentValidatorRule<R, V> rule) {
            rules.add(rule);
        }

        public void addCustom(CustomValidator FluentValidatorCustom) {
            customValidators.add(FluentValidatorCustom);
        }

        @Override
        public FluentValidator.Result validate(R rootObject, String property, V value) {
            for (BiFunction<R, V, Boolean> whenItem : when) {
                if (!whenItem.apply(rootObject, value)) {
                    return null;
                }
            }

            //todo unless

            for (FluentValidatorRule<R, V> rule : rules) {
                FluentValidator.Result result = rule.validate(rootObject, property, value);

                if (result != null && result.hasErrors()) {
                    return result;
                }
            }

            return null;
        }
    }

    @Data
    public static class RootData<R, V> extends ObjectData<R, V> {
        private boolean failFast;
    }

    @Data
    public static class ObjectData<R, V> extends BaseData<R, V> {
        private boolean notNull;


        public ObjectData notNull() {
            notNull = true;
            return this;
        }
    }

    public static class IntData<R> extends BaseData<R, Integer> {

    }

    @Setter
    @Getter
    public static class StringData<R> extends ObjectData<R, String> {
        private boolean url;
        private boolean mail;
        private Pattern matches;
    }

    public static class CollectionData<R, V> extends ObjectData<R, V> {

    }

    public static class PropertyUtils {

        public static String mix(String parent, String child) {
            return parent + "." + child;
        }

    }

    public static class ErrorUtils {

        public static List<FluentValidator.Error> addParentProperty(List<FluentValidator.Error> errors, String parentProperty) {
            List<FluentValidator.Error> answer = new ArrayList<>(errors.size());

            for (FluentValidator.Error error : errors) {
                answer.add(addParentProperty(error, parentProperty));
            }

            return answer;
        }

        public static FluentValidator.Error addParentProperty(FluentValidator.Error error, String parentProperty) {
            return new FluentValidator.Error(
                    PropertyUtils.mix(parentProperty, error.getProperty()),
                    error.getCode(),
                    error.getMessage(),
                    error.getValue()
            );
        }

    }
}
