package io.belov.soyuz.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 5/10/16.
 */
public class FluentValidatorBuilder<T> extends FluentValidatorObjects.BaseBuilder<T, T, FluentValidatorBuilder<T>, FluentValidatorObjects.RootData<T>> {

    private List<FluentValidatorObjects.FluentValidatorValidationData> validationDatas = new ArrayList<>();

    public FluentValidatorBuilder() {
        super(new FluentValidatorObjects.RootData<T>());
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

    private FluentValidatorBuilder<T> addFluentValidatorValidationData(String property, FluentValidatorObjects.FluentValidatorValidationData FluentValidatorValidationData) {
        validationDatas.add(FluentValidatorValidationData);
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


    public static class StringBuilder<P, V> {
        private FluentValidatorBuilder<P> builder;
        private String property;
        private FluentValidatorObjects.StringData data;

        public StringBuilder(FluentValidatorBuilder<P> builder, String property) {
            this.builder = builder;
            this.property = property;
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
        private FluentValidatorBuilder<P> builder;
        private String property;

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

}
