package io.belov.soyuz.validator;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 5/10/16.
 */
public class FluentValidatorBuilder<T> extends FluentValidatorObjects.BaseBuilder<T, T, FluentValidatorBuilder<T>, FluentValidatorObjects.RootData<T, T>> {

    private String rootProperty;
    private List<ValidationDataWithProperties> validationData = new ArrayList<>();

    public FluentValidatorBuilder() {
        this(null);
    }

    public FluentValidatorBuilder(String rootProperty) {
        super(new FluentValidatorObjects.RootData<T, T>());

        this.rootProperty = rootProperty;
    }

    public FluentValidatorBuilder<T> failFast() {
        return failFast(true);
    }

    public FluentValidatorBuilder<T> failFast(boolean failFast) {
        data.setFailFast(failFast);
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

    public StringBuilder<T> string(String property) {
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

    public static class IntBuilder<R> extends AbstractBuilder<R, Integer, IntBuilder<R>, FluentValidatorObjects.IntData<R>> {

        public IntBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.IntData(), property);
        }

        public IntBuilder<R> min(int min) {
            data.min(min);
            return this;
        }

        public IntBuilder<R> max(int max) {
            data.max(max);
            return this;
        }

    }


    public static class StringBuilder<R> extends AbstractObjectBuilder<R, String, StringBuilder<R>, FluentValidatorObjects.StringData<R>> {

        public StringBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.StringData(), property);
        }

        public StringBuilder<R> url() {
            data.setUrl(true);
            return this;
        }

        public StringBuilder<R> mail() {
            data.setMail(true);
            return this;
        }

        public StringBuilder<R> notEmpty() {
            data.addRule(new FluentValidatorRule.Str.NotEmpty());

            return this;
        }

        public StringBuilder<R> matches(Pattern pattern) {
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

    public static class ObjectBuilder<R, V> extends AbstractObjectBuilder<R, V, ObjectBuilder<R, V>, FluentValidatorObjects.ObjectData<R, V>> {
        public ObjectBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.ObjectData<R, V>(), property);
        }
    }

    private static abstract class AbstractObjectBuilder<R, V, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends AbstractBuilder<R, V, BuilderClass, DataClass> {
        public AbstractObjectBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(builder, data, property);
        }

        public BuilderClass notNull() {
            data.addRule(new FluentValidatorRule.Obj.NotNull<R, V>());

            return _this();
        }
    }

    private static abstract class AbstractBuilder<R, V, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends FluentValidatorObjects.BaseBuilder<R, V, BuilderClass, DataClass> {

        protected FluentValidatorBuilder<R> builder;
        protected String property;

        public AbstractBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(data);
            this.builder = builder;
            this.property = property;
        }

        public FluentValidatorBuilder<R> b() {
            return builder.addFluentValidatorValidationData(property, data);
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
