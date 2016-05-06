package io.belov.soyuz.validator

import io.belov.soyuz.validator.domain.Actress
import spock.lang.Specification

/**
 * Created by fbelov on 06.05.16.
 */
class FluentValidatorSpec extends Specification {

    private static final FluentValidator<Actress> actressValidator = Actress.validator

    def "should fail over"() {
        when:
        def actress = incorrectActress
        def answer = actressValidator.validate(actress)

        then:
        assert answer.hasErrors()
        assert !answer.isOk()
        assert answer.errors.size == 6
    }

    def "should fail fast"() {
        when:
        def actress = incorrectActress
        def answer = actressValidator.failFast().validate(actress)

        then:
        assert answer.hasErrors()
        assert !answer.isOk()
        assert answer.errors.size == 1
    }

    private getIncorrectActress() {

    }

}
