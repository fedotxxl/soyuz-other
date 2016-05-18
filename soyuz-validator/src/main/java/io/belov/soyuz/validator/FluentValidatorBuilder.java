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
 * Created by fbelov on 5/10/16.
 */
public class FluentValidatorBuilder<T> {

    private List<ValidationData> validationDatas = new ArrayList<>();

    private boolean failFast = false;

    public FluentValidatorBuilder<T> failFast() {
        failFast = true;
        return this;
    }

    public ObjectBuilder<T, Object> object(String property) {
        return new ObjectBuilder<>(this, property);
    }

    public <V> ObjectBuilder<T, V> object(String property, Class<V> clazz) {
        return new ObjectBuilder<>(this, property);
    }

    public StringBuilder<T, String> string(String property) {
        return new StringBuilder<>(this, property);
    }

    public CollectionBuilder<T, Object> collection(String property) {
        return new CollectionBuilder<>(this, property);
    }

    public <V> CollectionBuilder<T, V> collection(String property, Class<V> clazz) {
        return new CollectionBuilder<>(this, property);
    }

    public IntBuilder<T> i(String property) {
        return new IntBuilder<>(this, property);
    }

    public FluentValidator.Data<T> build() {
        return new FluentValidator.Data<T>(validationDatas);
    }

    private FluentValidatorBuilder<T> addValidationData(String property, ValidationData validationData) {
        validationDatas.add(validationData);
        return this;
    }

//    public static class ChainBuilder {
//        public ObjectData<Object, ObjectData> object() {
//            return new ObjectData<>();
//        }
//
//        public <V> ObjectData<Object, ObjectData> object(Class<V> clazz) {
//
//        }
//
//        public StringData<StringData> string() {
//            return new StringData<>();
//        }
//    }

    public static class IntBuilder<T> {
        private FluentValidatorBuilder<T> builder;
        private String property;
        private IntData data;

        public IntBuilder(FluentValidatorBuilder<T> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new IntData();
        }

        public IntBuilder<T> min(int min) {
            data.min(min);
            return this;
        }

        public IntBuilder<T> max(int max) {
            data.max(max);
            return this;
        }

        public FluentValidatorBuilder<T> b() {
            return builder.addValidationData(property, data);
        }
    }


    public static class StringBuilder<P, V> {
        private FluentValidatorBuilder<P> builder;
        private String property;
        private StringData data;

        public StringBuilder(FluentValidatorBuilder<P> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new StringData();
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addValidationData(property, data);
        }

        public StringBuilder<P, V> url() {
            data.setUrl(true);
            return this;
        }

        public StringBuilder<P, V> mail() {
            data.setMail(true);
            return this;
        }

        public StringBuilder<P, V> notEmpty() {
            data.setNotEmpty(true);
            return this;
        }

        public StringBuilder<P, V> matches(Pattern pattern) {
            data.setMatches(pattern);
            return this;
        }
    }

    public static class CollectionBuilder<P, V> {
        private FluentValidatorBuilder<P> builder;
        private String property;
        private CollectionData data;

        public CollectionBuilder(FluentValidatorBuilder<P> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new CollectionData();
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addValidationData(property, data);
        }

        public CollectionBuilder<P, V> notEmpty() {
            data.notEmpty();
            return this;
        }

        public CollectionBuilder<P, V> min(int min) {
            data.min(min);
            return this;
        }

        public CollectionBuilder<P, V> max(int max) {
            data.max(max);
            return this;
        }

        public CollectionBuilder<P, V> validator(FluentValidator.Data<V> validator) {
            data.setValidator(validator);
            return this;
        }

    }

    public static class ObjectBuilder<P, V> extends BaseBuilder<P, V, ObjectBuilder<P, V>, ObjectData<V>> {
        private FluentValidatorBuilder<P> builder;
        private String property;

        public ObjectBuilder(FluentValidatorBuilder<P> builder, String property) {
            super(new ObjectData());
            this.builder = builder;
            this.property = property;
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addValidationData(property, data);
        }

        public ObjectBuilder<P, V> notNull() {
            data.notNull();
            return this;
        }
    }

    private static class BaseBuilder<P, V, BuilderClass, DataClass extends BaseData<V>> {

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

        public BuilderClass custom(CustomValidatorSimple<P, V> customValidator) {
            data.addCustom(customValidator);

            return _this();
        }

        public BuilderClass custom(CustomValidatorWithBuilder<P, V> customValidatorWithBuilder) {
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

    public static interface CustomValidator {
    }

    public static interface CustomValidatorSimple<P, V> extends CustomValidator {
        FluentValidator.Result validate(P object, V propertyValue);
    }

    public static interface CustomValidatorWithBuilder<P, V> extends CustomValidator {
        FluentValidator.Result validate(P object, V propertyValue, FluentValidatorBuilder<V> fluentValidatorBuilder);
    }

    public static interface ValidationData {

    }

    public static class IntData implements ValidationData {
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

    @Data
    public static class ObjectData<T> extends BaseData<T> {
        private boolean notNull;


        public ObjectData notNull() {
            notNull = true;
            return this;
        }
    }

    @Getter
    @Setter
    public static class BaseData<V> implements ValidationData {
        private V eq;
        private Function<V, Boolean> eqFunction;
        private V notEq;
        private Function<V, Boolean> notEqFunction;
        private BiFunction when;
        private BiFunction unless;
        private FluentValidator.Data<V> validator;
        private final List<CustomValidator> customValidators = new ArrayList<>();
        private String message;

        public void addCustom(CustomValidator customValidator) {
            customValidators.add(customValidator);
        }
    }

}
