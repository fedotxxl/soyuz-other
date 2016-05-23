package io.belov.soyuz.validator

import io.belov.soyuz.validator.test.Actress
import spock.lang.Specification

import java.util.function.Function

/**
 * Created by fbelov on 06.05.16.
 */
class FluentValidatorSpec extends Specification {

    private static final FluentValidator.Data<Actress> actressValidator = Actress.validator

    def "should mix property names"() {
        when:
        def validator = FluentValidator.of("actress.sizes", Actress.Sizes)
                .i("height").min(4).max(5).b()
                .i("breast").min(1).b()
                .i("waist").min(1).max(999).b()
                .build()

        then:
        println(validator.validationData)

        assert validator.validationData
    }

    def "eq (simple)"() {
        when:
        def validator = FluentValidator.of(String).eq("hello world").build()

        then:
        assert validator.validate("hello") == FluentValidator.Result.failure("notEq", "hello")
        assert validator.validate("hello world") == FluentValidator.Result.success()
    }

    def "eq (function)"() {
        when:
        def validator = FluentValidator.of(String).eq({ it == "hello" || it == "world" } as Function).build()

        then:
        assert validator.validate("hello") == FluentValidator.Result.success()
        assert validator.validate("world") == FluentValidator.Result.success()
        assert validator.validate("hello world") == FluentValidator.Result.failure("notEq", "hello world")
    }
}
