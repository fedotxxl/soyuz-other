package io.belov.soyuz.validator

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by fbelov on 06.05.16.
 */
class FailFastFluentValidatorSpec extends Specification {

    def "should fail fast"() {
        setup:
        def car
        def result
        def checked = new AtomicBoolean()
        def validator = getValidator(false, checked)

        when:
        checked.set(false)
        car = new Car(title: "Lada", power: 150)
        result = validator.validate(car)

        then:
        assert result == FluentValidator.Result.success()
        assert checked.get() == true

        when:
        checked.set(false)
        car = new Car(power: 90)
        result = validator.validate(car)

        then:
        assert result == FluentValidator.Result.failure([new FluentValidator.Error("title", "notEmpty", null, null), new FluentValidator.Error("power", "min", null, 90)])
        assert checked.get() == true

        when:
        checked.set(false)
        car = new Car(power: 90)
        result = getValidator(true, checked).validate(car)

        then:
        assert result == FluentValidator.Result.failure("title", "notEmpty", null)
        assert checked.get() == false
    }

    private getValidator(boolean failFast, AtomicBoolean powerChecked) {
        return FluentValidator.of(Car)
                .failFast(failFast)
                .string("title").notEmpty().b()
                .i("power").custom(
                { car, power ->
                    powerChecked.set(true)

                    if (power > 100) {
                        return FluentValidator.Result.success()
                    } else {
                        return FluentValidator.Result.failure("power", "min", power)
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b()
                .build()
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
