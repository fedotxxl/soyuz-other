package io.belov.soyuz.validator

import spock.lang.Specification

class ValidatorFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def engineValidator = FluentValidator.of(CarEngine)
                .string("title").notEmpty().b()
                .i("power").min(1).max(999).b()
                .build()

        def carValidator = FluentValidator.of(Car)
                .string("title").notEmpty().b()
                .object("engine", CarEngine).notNull().validator(engineValidator).b()
                .build()

        then:
        assert carValidator.validate(new Car()) == FluentValidator.Result.failure(
                [
                        new FluentValidator.Error("title", "notEmpty", null),
                        new FluentValidator.Error("engine", "notNull", null)
                ]
        )

        assert carValidator.validate(new Car(title: "", engine: new CarEngine())) == FluentValidator.Result.failure(
                [
                        new FluentValidator.Error("title", "notEmpty", ""),
                        new FluentValidator.Error("engine.title", "notEmpty", null),
                        new FluentValidator.Error("engine.power", "min", 0)

                ]
        )

        assert carValidator.validate(new Car(title: "Lada", engine: new CarEngine(title: "v8", power: 113))) == FluentValidator.Result.success()
    }

    static class Car {
        private String title
        private CarEngine engine

        String getTitle() {
            return title
        }

        CarEngine getEngine() {
            return engine
        }
    }

    static class CarEngine {
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
