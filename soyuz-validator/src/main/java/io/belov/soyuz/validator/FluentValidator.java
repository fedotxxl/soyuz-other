package io.belov.soyuz.validator;

/**
 * Created by fbelov on 28.04.16.
 */
public class FluentValidator {

    //https://github.com/neoremind/fluent-validator
    //https://blog.aaronshaw.net/2014/02/03/simple-java-dsl-with-fluent-interface/
    //http://demeranville.com/csharp-fluent-validation-why-we-are-using-it/
    //https://github.com/JeremySkinner/FluentValidation
    //https://github.com/Jameskami/J8Validate
    //https://github.com/mat013/validation


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
