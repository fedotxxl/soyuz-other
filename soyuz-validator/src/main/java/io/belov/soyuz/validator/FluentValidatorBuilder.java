package io.belov.soyuz.validator;

import sun.util.resources.cldr.ss.CalendarData_ss_SZ;

import java.util.ArrayList;
import java.util.List;

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

    public IntBuilder<T> i(String property) {
        return new IntBuilder<>(this, property);
    }

    public FluentValidator.Data<T> build() {
        return new FluentValidator.Data<T>(validationDatas);
    }

    private FluentValidatorBuilder<T> addValidationData(ValidationData validationData) {
        validationDatas.add(validationData);
        return this;
    }

    public static class ChainBuilder {
        public ObjectData<Object, ObjectData> object() {
            return new ObjectData<>();
        }

        public <V> ObjectData<Object, ObjectData> object(Class<V> clazz) {

        }

        public StringData<StringData> string() {
            return new StringData<>();
        }
    }

    public static class IntBuilder<T> {
        private FluentValidatorBuilder<T> builder;
        private String property;
        private IntData intData;

        public IntBuilder(FluentValidatorBuilder<T> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.intData = new IntData();
        }

        public IntBuilder<T> min(int min) {
            intData.min(min);
            return this;
        }

        public IntBuilder<T> max(int max) {
            intData.max(max);
            return this;
        }

        public FluentValidatorBuilder<T> b() {
            return builder.addValidationData(this);
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
            return builder.addValidationData(this);
        }

        public StringBuilder<P, V> url() {
            data.url();
            return this;
        }

        public StringBuilder<P, V> notEmpty() {
            data.notEmpty();
            return this;
        }
    }

    public static class ObjectBuilder<P, V> {
        private FluentValidatorBuilder<P> builder;
        private String property;
        private ObjectData data;

        public ObjectBuilder(FluentValidatorBuilder<P> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new ObjectData();
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addValidationData(this);
        }

        public ObjectBuilder<P, V> notNull() {
            data.notNull();
            return this;
        }

        public ObjectBuilder<P, V> validator(FluentValidator.Data<V> validator) {
            data.validator(validator);
            return this;
        }

        public ObjectBuilder<P, V> custom(CustomValidator<P, V> customValidator) {
            data.custom(customValidator);
            return this;
        }

        public ObjectBuilder<P, V> custom(CustomValidatorWithBuilder<P, V> customValidatorWithBuilder) {
            data.custom(customValidator);
            return this;
        }
    }

    public static interface CustomValidator<P, V> {
        FluentValidator.Result validate(P object, V propertyValue);
    }

    public static interface CustomValidatorWithBuilder<P, V> {
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

    public static class StringData extends ObjectData {
        private boolean url;
        private boolean notEmpty;

        public StringData url() {
            this.url = true;
            return this;
        }

        public StringData notEmpty() {
            this.notEmpty = true;
            return this;
        }
    }

    public static class ObjectData implements ValidationData {
        private boolean notNull;
        private FluentValidator.Data<T> validator;
        private List<CustomValidator> customValidators = new ArrayList<>();

        public ObjectData notNull() {
            notNull = true;
            return this;
        }

        public ObjectData validator(FluentValidator.Data<T> validator) {
            this.validator = validator;
            return this;
        }

        public ObjectData custom(CustomValidator customValidator) {
            customValidators.add(customValidator);
            return this;
        }
    }

}
