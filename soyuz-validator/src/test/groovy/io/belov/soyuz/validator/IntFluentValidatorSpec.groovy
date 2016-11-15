package io.belov.soyuz.validator

import spock.lang.Specification

class IntFluentValidatorSpec extends Specification {

    def "min"() {
        when:
        def validator = FluentValidator.of(Car).i("power").min(1).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.failure(c, "power", "min", 0, [1] as Object[]) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
    }

    def "max"() {
        when:
        def validator = FluentValidator.of(Car).i("power").max(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "max", 1500, [999] as Object[]) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    def "min+max"() {
        when:
        def validator = FluentValidator.of(Car).i("power").min(1).max(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> FluentValidator.Result.failure(c, "power", "min", -1, [1] as Object[]) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "max", 1500, [999] as Object[]) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
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
