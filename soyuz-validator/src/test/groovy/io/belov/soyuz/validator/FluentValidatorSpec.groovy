package io.belov.soyuz.validator

import io.belov.soyuz.validator.test.Actress
import spock.lang.Specification

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

}
