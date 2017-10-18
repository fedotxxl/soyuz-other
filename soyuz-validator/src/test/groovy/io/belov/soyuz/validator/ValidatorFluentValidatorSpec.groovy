package io.belov.soyuz.validator

import spock.lang.Specification

class ValidatorFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def engineValidator = FluentValidator.of(CarEngine)
                .string("title").notEmpty().b()
                .i("power").greaterThan(1).lessThan(999).b()
                .build()

        def carValidator = FluentValidator.of(Car)
                .string("title").notEmpty().b()
                .object("engine", CarEngine).notNull().validator(engineValidator).b()
                .build()


        then:
        assert carValidator.validate(car) == result(car)

        where:
        car                                                                    | result
        new Car()                                                              | { c ->
            FluentValidator.Result.failure(
                    c,
                    [
                            new FluentValidator.Error("title", "notEmpty", null),
                            new FluentValidator.Error("engine", "notNull", null)
                    ]
            )
        }

        new Car(title: "", engine: new CarEngine())                            | { c ->
            FluentValidator.Result.failure(
                    c,
                    [
                            new FluentValidator.Error("title", "notEmpty", ""),
                            new FluentValidator.Error("engine.title", "notEmpty", null),
                            new FluentValidator.Error("engine.power", "greaterThan", 0, 1)

                    ])
        }

        new Car(title: "Lada", engine: new CarEngine(title: "v8", power: 113)) | { c -> FluentValidator.Result.success(c) }
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
