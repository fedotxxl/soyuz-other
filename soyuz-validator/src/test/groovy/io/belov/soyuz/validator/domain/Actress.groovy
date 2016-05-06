package io.belov.soyuz.validator.domain

import io.belov.soyuz.validator.FluentValidator

/**
 * Created by fbelov on 06.05.16.
 */
class Actress {

    private TagTranslation en
    private TagTranslation ja
    private String blogUrl
    private Set<Tag> tags
    private Sizes sizes

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

    static FluentValidator<Actress> getValidator() {
        def tagValidator = Tag.validator
        def tagTranslationValidator = TagTranslation.validator

        def tagTranslationChain = FluentValidator.chain().object().notNull().validator(tagTranslationValidator)

        return FluentValidator.for(Actress)
                .failFast()
                .object("en").chain(tagTranslationChain).b()
                .object("ja").chain(tagTranslationChain).b()
                .string("blogUrl").url().notEmpty().b()
                .collection("tags").validator(tagValidator).b()
                .object("sizes").notNull().custom(
                { o, value ->
                    return FluentValidator.for(Actress.Sizes)
                            .i("height").min(1).b()
                            .i("breast").min(1).b()
                            .i("waist").min(1).max(999).b()
                            .build()
                            .validate(value)
                }).b()
                .build()
    }

    public static class Sizes {
        private int height
        private int breast
        private int waist
        private int hip
    }
}
