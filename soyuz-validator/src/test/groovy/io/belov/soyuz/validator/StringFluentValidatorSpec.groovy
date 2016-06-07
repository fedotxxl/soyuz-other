package io.belov.soyuz.validator

import spock.lang.Specification

class StringFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = FluentValidator.of(Car).string("title").notEmpty().b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                    | result
        new Car()              | { c -> FluentValidator.Result.failure(c, "title", "notEmpty", null) }
        new Car(title: "")     | { c -> FluentValidator.Result.failure(c, "title", "notEmpty", "") }
        new Car(title: "Lada") | { c -> FluentValidator.Result.success(c) }
    }

    static class Car {
        private String title
        private int power

        int getPower() {
            return power
        }

        String getTitle() {
            return title
        }
    }
}
