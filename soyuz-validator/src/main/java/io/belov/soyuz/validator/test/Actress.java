package io.belov.soyuz.validator.test;

import io.belov.soyuz.validator.FluentValidator;
import io.belov.soyuz.validator.FluentValidatorBuilder;

import java.util.Set;

/**
 * Created by fbelov on 06.05.16.
 */
class Actress {

    private TagTranslation en;
    private TagTranslation ja;
    private String blogUrl;
    private Set<Tag> tags;
    private Sizes sizes;

//    FluentValidator tagTranslationValidator = validate(TagTranslation.class)
//            .string("title").notEmpty().build();
//
//    FluentValidator v = validate(FluentValidator.class)
//            .integer("name").notNull().isGreaterThan(20).back()
//            .string("hello").custom((a) -> {
//
//    }).back()
//            .custom((o, context) -> {
//        Validator(context)
//                .string("property", o.getName()).notEmpty().back()
//                .integer("another", o.getAge()).isGreaterThan(20).back()
//                .object("trololo", o.getObject()).validator(vv)
//                .validate();
//    })
//            .list("tagTranslation").validator(tagTranslationValidator).back()
//            .build();

    static FluentValidator.Data<Actress> getValidator() {
        Object tagValidator = null;
        Object tagTranslationChain = FluentValidator.chain().object(TagTranslation.class).notNull().validator(tagTranslationValidator);
        FluentValidator.Data<TagTranslation> tagTranslationValidator = TagTranslation.getValidator();

        return FluentValidator.of(Actress.class)
                .failFast()
                .object("en", TagTranslation.class).validator(tagTranslationValidator).b()
                .object("ja", TagTranslation.class).validator(tagTranslationValidator).b()
                .string("blogUrl").url().notEmpty().b()
//                .collection("tags").validator(tagValidator).b()
                .object("sizes", Actress.Sizes.class).notNull().custom((o, value, validator) ->  {
                    return validator
                            .i("height").min(4).max(5).b()
                            .i("breast").min(1).b()
                            .i("waist").min(1).max(999).b()
                            .build()
                            .validate(value);
                }).b()
                .build();
    }

    public static class Sizes {
        private int height;
        private int breast;
        private int waist;
        private int hip;
    }
}
