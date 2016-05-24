package io.belov.soyuz.validator

import spock.lang.Specification

class CustomFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def validator = FluentValidator.of(Car).i("power").custom(
                { car, power ->
                    if (power > 100) {
                        return FluentValidator.Result.success()
                    } else {
                        return FluentValidator.Result.failure("power", "min", power)
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(new Car(power: 150)) == FluentValidator.Result.success()
        assert validator.validate(new Car(power: 90)) == FluentValidator.Result.failure("power", "min", 90)
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
