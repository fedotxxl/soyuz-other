package io.belov.soyuz.validator;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 5/10/16.
 */
public class FluentValidatorBuilder<T> extends FluentValidatorObjects.BaseBuilder<T, T, FluentValidatorBuilder<T>, FluentValidatorObjects.RootData<T>> {

    private String rootProperty;
    private List<ValidationDataWithProperties> validationData = new ArrayList<>();

    public FluentValidatorBuilder() {
        this(null);
    }

    public FluentValidatorBuilder(String rootProperty) {
        super(new FluentValidatorObjects.RootData<T>());

        this.rootProperty = rootProperty;
    }

    public FluentValidatorBuilder<T> failFast() {
        data.setFailFast(true);
        return this;
    }

    public FluentValidatorBuilder<T> notNull() {
        data.notNull();
        return this;
    }

    public ObjectBuilder<T, Object> object(String property) {
        return new ObjectBuilder<>(this, getFullProperty(property));
    }

    public <V> ObjectBuilder<T, V> object(String property, Class<V> clazz) {
        return new ObjectBuilder<>(this, getFullProperty(property));
    }

    public StringBuilder<T, String> string(String property) {
        return new StringBuilder<>(this, getFullProperty(property));
    }

    public CollectionBuilder<T, Object> collection(String property) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public <V> CollectionBuilder<T, V> collection(String property, Class<V> clazz) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public IntBuilder<T> i(String property) {
        return new IntBuilder<>(this, getFullProperty(property));
    }

    public FluentValidator.Data<T> build() {
        validationData.add(new ValidationDataWithProperties(null, data));

        return new FluentValidator.Data<T>(validationData);
    }

    private FluentValidatorBuilder<T> addFluentValidatorValidationData(String property, FluentValidatorObjects.FluentValidatorValidationData validationData) {
        this.validationData.add(new ValidationDataWithProperties(property, validationData));

        return this;
    }

    private String getFullProperty(String property) {
        if (rootProperty == null) {
            return property;
        } else {
            return rootProperty + "." + property;
        }
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
        private FluentValidatorObjects.IntData data;

        public IntBuilder(FluentValidatorBuilder<T> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new FluentValidatorObjects.IntData();
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
            return builder.addFluentValidatorValidationData(property, data);
        }
    }


    public static class StringBuilder<P, V> extends ObjectBuilder<P, V> {
        private FluentValidatorObjects.StringData data;

        public StringBuilder(FluentValidatorBuilder<P> builder, String property) {
            super(builder, property);
            this.data = new FluentValidatorObjects.StringData();
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addFluentValidatorValidationData(property, data);
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
            data.addRule(new FluentValidatorRule.Str.NotEmpty());

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
        private FluentValidatorObjects.CollectionData data;

        public CollectionBuilder(FluentValidatorBuilder<P> builder, String property) {
            this.builder = builder;
            this.property = property;
            this.data = new FluentValidatorObjects.CollectionData();
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addFluentValidatorValidationData(property, data);
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

    public static class ObjectBuilder<P, V> extends FluentValidatorObjects.BaseBuilder<P, V, ObjectBuilder<P, V>, FluentValidatorObjects.ObjectData<V>> {
        protected FluentValidatorBuilder<P> builder;
        protected String property;

        public ObjectBuilder(FluentValidatorBuilder<P> builder, String property) {
            super(new FluentValidatorObjects.ObjectData());
            this.builder = builder;
            this.property = property;
        }

        public FluentValidatorBuilder<P> b() {
            return builder.addFluentValidatorValidationData(property, data);
        }

        public ObjectBuilder<P, V> notNull() {
            data.notNull();
            return this;
        }
    }

    @ToString
    public static class ValidationDataWithProperties {
        private String property;
        private FluentValidatorObjects.FluentValidatorValidationData data;

        public ValidationDataWithProperties(String property, FluentValidatorObjects.FluentValidatorValidationData data) {
            this.property = property;
            this.data = data;
        }

        @Nullable
        public String getProperty() {
            return property;
        }

        public FluentValidatorObjects.FluentValidatorValidationData getData() {
            return data;
        }
    }
}
