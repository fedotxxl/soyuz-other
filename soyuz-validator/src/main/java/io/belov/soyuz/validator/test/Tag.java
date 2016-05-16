package io.belov.soyuz.validator.test;

import io.belov.soyuz.validator.FluentValidator;

/**
 * Created by fbelov on 06.05.16.
 */
class Tag {

    private TagTranslation en;
    private TagTranslation ja;

    public static FluentValidator.Data<Tag> getValidator() {
        return FluentValidator.of(Tag.class)
                .object("en", TagTranslation.class).notNull().validator(TagTranslation.getValidator()).b()
                .object("ja", TagTranslation.class).notNull().validator(TagTranslation.getValidator())
                .when((o, v) -> {
                    return o != null;
                })
                .b()
                .build();
    }

}
