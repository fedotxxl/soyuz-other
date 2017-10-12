package io.belov.soyuz.validator

import spock.lang.Specification

class IntFluentValidatorSpec extends Specification {

    def "greaterThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterThan(1).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", 0, [1] as Object[]) }
        new Car(power: 1)   | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", 1, [1] as Object[]) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterOrEqual(1).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.failure(c, "power", "greaterOrEqual", 0, [1] as Object[]) }
        new Car(power: 1)   | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
    }

    def "lessThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 1500, [999] as Object[]) }
        new Car(power: 999)  | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 999, [999] as Object[]) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    def "lessOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).i("power").lessOrEqual(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessOrEqual", 1500, [999] as Object[]) }
        new Car(power: 999)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    def "greaterThan+lessThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterThan(1).lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> FluentValidator.Result.failure(c, "power", "greaterThan", -1, [1] as Object[]) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, "power", "lessThan", 1500, [999] as Object[]) }
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
