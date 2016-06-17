package io.belov.soyuz.validator

import spock.lang.Specification

class DateFluentValidatorSpec extends Specification {

    def "before / after"() {
        when:
        def validator = FluentValidator.of(Car)
                .date("constructed").after(new Date(1987 - 1900, 01, 01)).before(new Date(1987 - 1900, 12, 31)).b()
                .build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                 | result
        new Car()                                           | { c -> FluentValidator.Result.success(c) }
        new Car(constructed: new Date(1986 - 1900, 12, 31)) | { c -> FluentValidator.Result.failure(c, "constructed", "after", c.constructed) }
        new Car(constructed: new Date(1988 - 1900, 01, 01)) | { c -> FluentValidator.Result.failure(c, "constructed", "before", c.constructed) }
        new Car(constructed: new Date(1987 - 1900, 05, 01)) | { c -> FluentValidator.Result.success(c) }
    }

    def "between"() {
        when:
        def validator = FluentValidator.of(Car)
                .date("constructed").between(new Date(1987 - 1900, 01, 01), new Date(1987 - 1900, 12, 31)).b()
                .build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                 | result
        new Car()                                           | { c -> FluentValidator.Result.success(c) }
        new Car(constructed: new Date(1986 - 1900, 12, 31)) | { c -> FluentValidator.Result.failure(c, "constructed", "between", c.constructed) }
        new Car(constructed: new Date(1988 - 1900, 01, 01)) | { c -> FluentValidator.Result.failure(c, "constructed", "between", c.constructed) }
        new Car(constructed: new Date(1987 - 1900, 05, 01)) | { c -> FluentValidator.Result.success(c) }
    }

    static class Car {
        String title
        Date constructed
    }
}
