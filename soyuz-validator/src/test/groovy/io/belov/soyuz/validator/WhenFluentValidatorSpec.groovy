package io.belov.soyuz.validator

import spock.lang.Specification

import java.util.function.BiFunction

class WhenFluentValidatorSpec extends Specification {

    def "when (simple)"() {
        when:
        def validator = FluentValidator.of(Car)
                .string("title").notEmpty().when({ car, title -> car.power > 100 } as BiFunction).b()
                .build()

        then:
        assert validator.validate(new Car(power: 50)) == FluentValidator.Result.success() //skip by when
        assert validator.validate(new Car(title: "Lada", power: 150)) == FluentValidator.Result.success() //correct title
        assert validator.validate(new Car(power: 150)) == FluentValidator.Result.success()
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
