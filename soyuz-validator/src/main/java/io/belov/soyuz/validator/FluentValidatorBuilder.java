package io.belov.soyuz.validator;

import lombok.ToString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
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

    public DateBuilder<T> date(String property) {
        return new DateBuilder<>(this, getFullProperty(property));
    }

    public CollectionBuilder<T, Object> collection(String property) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public <V> CollectionBuilder<T, V> collection(String property, Class<V> clazz) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public PrimitiveIntBuilder<T> i(String property) {
        return new PrimitiveIntBuilder<>(this, getFullProperty(property));
    }

    public IntegerBuilder<T> integer(String property) {
        return new IntegerBuilder<>(this, getFullProperty(property));
    }

    public LongBuilder<T> long_(String property) {
        return new LongBuilder<>(this, getFullProperty(property));
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

    public static class PrimitiveIntBuilder<R> extends AbstractBuilder<R, Integer, PrimitiveIntBuilder<R>, FluentValidatorObjects.IntData<R>> {

        public PrimitiveIntBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.IntData(), property);
        }

        public PrimitiveIntBuilder<R> min(int min) {
            data.addRule(new FluentValidatorRule.Int.Min<>(min));
            return this;
        }

        public PrimitiveIntBuilder<R> max(int max) {
            data.addRule(new FluentValidatorRule.Int.Max<>(max));
            return this;
        }

    }

    public static class IntegerBuilder<R> extends AbstractNumberBuilder<R, Integer, IntegerBuilder<R>, FluentValidatorObjects.NumberData<R, Integer>> {
        public IntegerBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.NumberData<R, Integer>(), property);
        }
    }

    public static class LongBuilder<R> extends AbstractNumberBuilder<R, Long, IntegerBuilder<R>, FluentValidatorObjects.NumberData<R, Long>> {
        public LongBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.NumberData<R, Long>(), property);
        }
    }

    public static class StringBuilder<R> extends AbstractObjectBuilder<R, String, StringBuilder<R>, FluentValidatorObjects.StringData<R>> {

        public StringBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.StringData<>(), property);
        }

        public StringBuilder<R> url() {
            data.setUrl(true);
            return this;
        }

        public StringBuilder<R> mail() {
            data.setMail(true);
            return this;
        }

        public StringBuilder<R> filePath() {
            //http://stackoverflow.com/questions/893977/java-how-to-find-out-whether-a-file-name-is-valid
            //todo
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

    public static class DateBuilder<R> extends AbstractObjectBuilder<R, Date, DateBuilder<R>, FluentValidatorObjects.DateData<R>> {

        public DateBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.DateData<>(), property);
        }

        public DateBuilder<R> before(Date date) {
            return before(() -> date);
        }

        public DateBuilder<R> before(Supplier<Date> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.Before<>(dateSupplier));

            return this;
        }

        public DateBuilder<R> after(Date date) {
            return after(() -> date);
        }

        public DateBuilder<R> after(Supplier<Date> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.After<>(dateSupplier));

            return this;
        }

        public DateBuilder<R> between(Date after, Date before) {
            return between(() -> after, () -> before);
        }

        public DateBuilder<R> between(Supplier<Date> afterSupplier, Supplier<Date> beforeSupplier) {
            data.addRule(new FluentValidatorRule.D.Between<>(afterSupplier, beforeSupplier));

            return this;
        }
    }

    public static class CollectionBuilder<R, V> extends AbstractObjectBuilder<R, Collection<V>, CollectionBuilder<R, V>, FluentValidatorObjects.CollectionData<R, Collection<V>>> {

        public CollectionBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.CollectionData<>(), property);
        }

        public CollectionBuilder<R, V> notEmpty() {
            data.addRule(new FluentValidatorRule.Coll.NotEmpty<>());

            return this;
        }

        public CollectionBuilder<R, V> min(int min) {
            data.addRule(new FluentValidatorRule.Coll.Min<>(min));

            return this;
        }

        public CollectionBuilder<R, V> max(int max) {
            data.addRule(new FluentValidatorRule.Coll.Max<>(max));

            return this;
        }

        public CollectionBuilder<R, V> itemValidator(FluentValidator.Data<V> validator) {
            data.addRule(new FluentValidatorRule.Coll.ItemValidator<>(validator));

            return _this();
        }
    }

    public static class ObjectBuilder<R, V> extends AbstractObjectBuilder<R, V, ObjectBuilder<R, V>, FluentValidatorObjects.ObjectData<R, V>> {
        public ObjectBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.ObjectData<>(), property);
        }
    }

    private static class AbstractNumberBuilder<R, V extends Number & Comparable<V>, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends AbstractObjectBuilder<R, V, BuilderClass, DataClass> {

        public AbstractNumberBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(builder, data, property);
        }

        public BuilderClass min(V min) {
            data.addRule(new FluentValidatorRule.N.Min<>(min));

            return _this();
        }

        public BuilderClass max(V max) {
            data.addRule(new FluentValidatorRule.N.Max<>(max));

            return _this();
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
