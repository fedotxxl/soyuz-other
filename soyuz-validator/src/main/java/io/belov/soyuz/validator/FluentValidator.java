package io.belov.soyuz.validator;

import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 28.04.16.
 */
public class FluentValidator<T> {

    private static final PropertyUtilsBean PROPERTY_UTILS_BEAN = BeanUtilsBean.getInstance().getPropertyUtils();

    //https://github.com/JeremySkinner/FluentValidation

    public static <T> FluentValidatorBuilder<T> of(Class<T> clazz) {
        return new FluentValidatorBuilder<>();
    }

    public static <T> FluentValidatorBuilder<T> of(String property, Class<T> clazz) {
        return new FluentValidatorBuilder<>(property);
    }

//    public static FluentValidatorBuilder.ChainBuilder chain() {
//        return new FluentValidatorBuilder.ChainBuilder();
//    }

    @ToString
    public static class Data<T> {
        private List<FluentValidatorBuilder.ValidationDataWithProperties> validationData = new ArrayList<>();

        public Data(List<FluentValidatorBuilder.ValidationDataWithProperties> validationData) {
            this.validationData = validationData;
        }

        public List<FluentValidatorBuilder.ValidationDataWithProperties> getValidationData() {
            return validationData;
        }

        public FluentValidator.Result validate(T o) {
            List<Error> errors = new ArrayList<>();

            for (FluentValidatorBuilder.ValidationDataWithProperties validationDataWithProperties : validationData) {
                String property = validationDataWithProperties.getProperty();
                Object value = getPropertyValue(o, property);

                FluentValidator.Result result = validationDataWithProperties.getData().validate(o, property, value);

                if (result != null) {
                    errors.addAll(result.getErrors());
                }
            }

            return FluentValidator.Result.failure(errors);
        }

        private Object getPropertyValue(T o, String property) {
            try {
                return (property == null) ? o : PROPERTY_UTILS_BEAN.getNestedProperty(o, property);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Result {
        private List<Error> errors;

        private Result(List<Error> errors) {
            this.errors = errors;
        }

        public boolean isOk() {
            return errors.isEmpty();
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public List<Error> getErrors() {
            return errors;
        }

        public static Result success() {
            return new Result(new ArrayList<>());
        }

        public static <V> Result failure(String code, V value) {
            return failure(null, code, value);
        }

        public static <V> Result failure(String property, String code, V value) {
            return failure(new Error<>(property, code, null, value));
        }

        public static Result failure(Error error) {
            List<Error> answer = new ArrayList<>();

            answer.add(error);

            return new Result(answer);
        }

        public static Result failure(List<Error> errors) {
            return new Result(errors);
        }
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class Error<V> {
        private String property;
        private String code;
        private String message;
        private V value;

        public Error(String property, String code, String message, V value) {
            this.property = property;
            this.code = code;
            this.message = message;
            this.value = value;
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
