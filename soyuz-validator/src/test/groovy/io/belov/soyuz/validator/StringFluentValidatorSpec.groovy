package io.belov.soyuz.validator
import spock.lang.Specification

class StringFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = FluentValidator.of(Car).string("title").notEmpty().b().build()

        then:
        assert validator.validate(new Car()) == FluentValidator.Result.failure("title", "notEmpty", null)
        assert validator.validate(new Car(title: "")) == FluentValidator.Result.failure("title", "notEmpty", "")
        assert validator.validate(new Car(title: "Lada")) == FluentValidator.Result.success()
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
