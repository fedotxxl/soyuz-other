package io.belov.soyuz.validator

import spock.lang.Specification

class CustomFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def validator = FluentValidator.of(Car).i("power").custom(
                { c, power ->
                    if (power > 100) {
                        return FluentValidator.Result.success(c)
                    } else {
                        return FluentValidator.Result.failure(c, "power", "min", power)
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car | result
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
                new Car(power: 90) | { c -> FluentValidator.Result.failure(c, "power", "min", 90) }
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
