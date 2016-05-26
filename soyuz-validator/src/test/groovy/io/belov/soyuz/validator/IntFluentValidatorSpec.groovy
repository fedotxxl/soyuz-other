package io.belov.soyuz.validator
import spock.lang.Specification

class IntFluentValidatorSpec extends Specification {

    def "min"() {
        when:
        def validator = FluentValidator.of(Car).i("power").min(1).b().build()

        then:
        assert validator.validate(new Car()) == FluentValidator.Result.failure("power", "min", 0)
        assert validator.validate(new Car(power: 150)) == FluentValidator.Result.success()
    }

    def "max"() {
        when:
        def validator = FluentValidator.of(Car).i("power").max(999).b().build()

        then:
        assert validator.validate(new Car(power: 1500)) == FluentValidator.Result.failure("power", "max", 1500)
        assert validator.validate(new Car(power: 150)) == FluentValidator.Result.success()
    }

    def "min+max"() {
        when:
        def validator = FluentValidator.of(Car).i("power").min(1).max(999).b().build()

        then:
        assert validator.validate(new Car(power: -1)) == FluentValidator.Result.failure("power", "min", -1)
        assert validator.validate(new Car(power: 1500)) == FluentValidator.Result.failure("power", "max", 1500)
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
