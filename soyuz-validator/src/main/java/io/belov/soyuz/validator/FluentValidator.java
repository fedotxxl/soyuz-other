package io.belov.soyuz.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 28.04.16.
 */
public class FluentValidator<T> {

    //https://github.com/neoremind/fluent-validator
    //https://blog.aaronshaw.net/2014/02/03/simple-java-dsl-with-fluent-interface/
    //http://demeranville.com/csharp-fluent-validation-why-we-are-using-it/
    //https://github.com/JeremySkinner/FluentValidation
    //https://github.com/Jameskami/J8Validate
    //https://github.com/mat013/validation

    public static <T> FluentValidatorBuilder<T> of(Class<T> clazz) {
        return new FluentValidatorBuilder<>();
    }

    public static FluentValidatorBuilder.ChainBuilder chain() {
        return new FluentValidatorBuilder.ChainBuilder();
    }

    public static class Data<T> {
        private List<FluentValidatorBuilder.ValidationData> validationDatas = new ArrayList<>();

        public Data(List<FluentValidatorBuilder.ValidationData> validationDatas) {
            this.validationDatas = validationDatas;
        }

        public FluentValidator.Result validate(T o) {

        }
    }

    public static class Result {

    }

    public static void main(String[] args) {
        FluentValidator tagTranslationValidator = validate(TagTranslation.class)
                .string("title").notEmpty().build();

        FluentValidator v = validate(FluentValidator.class)
                .integer("name").notNull().isGreaterThan(20).back()
                .string("hello").custom((a) -> {

        }).back()
                .custom((o, context) -> {
                    Validator(context)
                            .string("property", o.getName()).notEmpty().back()
                            .integer("another", o.getAge()).isGreaterThan(20).back()
                            .object("trololo", o.getObject()).validator(vv)
                            .validate();
                })
                .list("tagTranslation").validator(tagTranslationValidator).back()
                .build();



    }

}
