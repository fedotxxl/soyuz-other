package io.belov.soyuz.validator;

import lombok.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fbelov on 28.04.16.
 */
public interface FluentValidator<T> {

    //https://github.com/JeremySkinner/FluentValidation

    FluentValidator.Result<T> validate(T rootObject);

    static <T> FluentValidatorBuilder<T> of(Class<T> clazz) {
        return new FluentValidatorBuilder<>();
    }

    static <T> FluentValidatorBuilder<T> of(String property, Class<T> clazz) {
        return new FluentValidatorBuilder<>(property);
    }

//    public static FluentValidatorBuilder.ChainBuilder chain() {
//        return new FluentValidatorBuilder.ChainBuilder();
//    }

    @ToString
    class Data<R> implements FluentValidator<R> {
        private static final PropertyUtilsBean PROPERTY_UTILS_BEAN = BeanUtilsBean.getInstance().getPropertyUtils();

        private List<FluentValidatorBuilder.ValidationDataWithProperties> validationData = new ArrayList<>();

        public Data(List<FluentValidatorBuilder.ValidationDataWithProperties> validationData) {
            this.validationData = validationData;
        }

        public List<FluentValidatorBuilder.ValidationDataWithProperties> getValidationData() {
            return validationData;
        }

        public FluentValidator.Result<R> validate(R rootObject) {
            List<Error> errors = new ArrayList<>();

            for (FluentValidatorBuilder.ValidationDataWithProperties validationDataWithProperties : validationData) {
                String property = validationDataWithProperties.getProperty();
                Object value = getPropertyValue(rootObject, property);

                FluentValidator.Result result = validationDataWithProperties.getData().validate(rootObject, property, value);

                if (result != null) {
                    errors.addAll(result.getErrors());
                }
            }

            return FluentValidator.Result.failure(rootObject, errors);
        }

        private Object getPropertyValue(R o, String property) {
            try {
                if (o == null) {
                    return null;
                } else if (property == null) {
                    return o;
                } else {
                    return PROPERTY_UTILS_BEAN.getNestedProperty(o, property);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Result<R> {
        private static final Result SUCCESS = new Result(null, Collections.unmodifiableList(new ArrayList<>()));

        private R rootObject;
        private List<Error> errors;

        private Result(R rootObject, List<Error> errors) {
            this.rootObject = rootObject;
            this.errors = errors;
        }

        public boolean isOk() {
            return errors.isEmpty();
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public R getRootObject() {
            return rootObject;
        }

        public List<Error> getErrors() {
            return errors;
        }

        public static Result success() {
            return SUCCESS;
        }

        public static <R> Result<R> success(R rootObject) {
            return new Result<>(rootObject, new ArrayList<>());
        }

        public static <R, V> Result<R> failure(R rootObject, String code, V value) {
            return failure(rootObject, code, value, null);
        }

        public static <R, V> Result<R> failure(R rootObject, String code, V value, Object[] args) {
            return failure(rootObject, null, code, value, args);
        }

        public static <R, V> Result<R> failure(R rootObject, String property, String code, V value) {
            return failure(rootObject, property, code, value, null);
        }

        public static <R, V> Result<R> failure(R rootObject, String property, String code, V value, Object[] args) {
            return failure(rootObject, new Error<>(property, code, value, args));
        }

        public static <R> Result<R> failure(R rootObject, Error error) {
            List<Error> answer = new ArrayList<>();

            answer.add(error);

            return new Result<>(rootObject, answer);
        }

        public static <R> Result<R> failure(R rootObject, List<Error> errors) {
            return new Result<>(rootObject, errors);
        }

        public void ifHasErrorsThrowAnException() {
            if (hasErrors()) {
                throw new ValidationException(rootObject, errors);
            }
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ValidationException extends RuntimeException {
        private Object rootObject;
        private List<Error> errors;
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class Error<V> {

        private String code;
        @Nullable
        private V value;
        @Nullable
        private String property;
        @Nullable
        private Object[] args;

        public Error(String code, V value) {
            this(null, code, value);
        }

        public Error(String property, String code, V value) {
            this(property, code, value, null);
        }

        public Error(String property, String code, V value, Object[] args) {
            this.property = property;
            this.code = code;
            this.value = value;
            this.args = args;
        }

        public boolean hasProperty() {
            return property != null && property.length() > 0;
        }

        public boolean hasArgs() {
            return args != null;
        }

        public boolean hasValue() {
            return value != null;
        }
    }

//    public static void main(String[] args) {
//        FluentValidator tagTranslationValidator = validate(TagTranslation.class)
//                .string("title").notEmpty().build();
//
//        FluentValidator v = validate(FluentValidator.class)
//                .integer("name").notNull().isGreaterThan(20).back()
//                .string("hello").custom((a) -> {
//
//        }).back()
//                .custom((o, context) -> {
//                    Validator(context)
//                            .string("property", o.getName()).notEmpty().back()
//                            .integer("another", o.getAge()).isGreaterThan(20).back()
//                            .object("trololo", o.getObject()).validator(vv)
//                            .validate();
//                })
//                .list("tagTranslation").validator(tagTranslationValidator).back()
//                .build();
//
//
//
//    }

}
