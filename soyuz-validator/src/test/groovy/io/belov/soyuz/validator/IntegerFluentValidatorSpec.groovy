package io.belov.soyuz.validator

import spock.lang.Specification

class IntegerFluentValidatorSpec extends Specification {

    def "min"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").min(50).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
        new Car(power: 10)  | { c -> FluentValidator.Result.failure(c, "power", "min", 10) }
    }

    def "max"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").max(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car()            | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "max", 1500) }
    }

    def "min+max"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").min(1).max(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> FluentValidator.Result.failure(c, "power", "min", -1) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "max", 1500) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    static class Car {
        private String title
        private Integer power

        Integer getPower() {
            return power
        }

        String getTitle() {
            return title
        }
    }
}
