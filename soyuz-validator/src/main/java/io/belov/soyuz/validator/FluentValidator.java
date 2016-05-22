package io.belov.soyuz.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 28.04.16.
 */
public class FluentValidator<T> {

    //https://github.com/JeremySkinner/FluentValidation

    public static <T> FluentValidatorBuilder<T> of(Class<T> clazz) {
        return new FluentValidatorBuilder<>();
    }

//    public static FluentValidatorBuilder.ChainBuilder chain() {
//        return new FluentValidatorBuilder.ChainBuilder();
//    }

    public static class Data<T> {
        private List<FluentValidatorObjects.FluentValidatorValidationData> validationDatas = new ArrayList<>();

        public Data(List<FluentValidatorObjects.FluentValidatorValidationData> validationDatas) {
            this.validationDatas = validationDatas;
        }

        public FluentValidator.Result validate(T o) {
            return null;
        }
    }

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

        public static Result failure(Error error) {
            List<Error> answer = new ArrayList<>();

            answer.add(error);

            return new Result(answer);
        }

        public static Result failure(List<Error> errors) {
            return new Result(errors);
        }
    }

    public static class Error {
        private String property;
        private String code;
        private String message;
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
