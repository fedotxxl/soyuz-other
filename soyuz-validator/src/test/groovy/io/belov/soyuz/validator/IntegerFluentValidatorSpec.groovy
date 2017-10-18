package io.belov.soyuz.validator

import spock.lang.Specification

class IntegerFluentValidatorSpec extends Specification {

    def "greaterThan"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").greaterThan(50).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
        new Car(power: 50)  | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", 50, 50) }
        new Car(power: 10)  | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", 10, 50) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").greaterOrEqual(50).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
        new Car(power: 50)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 10)  | { c -> FluentValidator.Result.failure(c, "power", "greaterOrEqual", 10, 50) }
    }

    def "lessThan"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car()            | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 999)  | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 999, 999) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 1500, 999) }
    }

    def "lessOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").lessOrEqual(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car()            | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 999)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessOrEqual", 1500, 999) }
    }

    def "greaterThan+lessThan"() {
        when:
        def validator = FluentValidator.of(Car).integer("power").greaterThan(1).lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", -1, 1) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 1500, 999) }
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
