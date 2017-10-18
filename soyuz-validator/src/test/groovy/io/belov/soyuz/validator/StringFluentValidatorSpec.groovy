package io.belov.soyuz.validator

import spock.lang.Specification

class StringFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = FluentValidator.of(Car).string("title").notEmpty().b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                    | result
        new Car()              | { c -> FluentValidator.Result.failure(c, "title", "notEmpty", null) }
        new Car(title: "")     | { c -> FluentValidator.Result.failure(c, "title", "notEmpty", "") }
        new Car(title: "Lada") | { c -> FluentValidator.Result.success(c) }
    }

    def "isBoolean"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isBoolean().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isBoolean", title)
        }

        where:
        title   | result
        null    | false
        ""      | false
        "abc"   | false
        "true"  | true
        "tRue"  | true
        "false" | true
        "FALSE" | true
    }

    def "isByte"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isByte().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isByte", title)
        }

        where:
        title  | result
        null   | false
        ""     | false
        "abc"  | false
        "-129" | false
        "-128" | true
        "0"    | true
        "127"  | true
        "128"  | false
        "12a8" | false
    }

    def "isShort"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isShort().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isShort", title)
        }

        where:
        title    | result
        null     | false
        ""       | false
        "abc"    | false
        "-32769" | false
        "-32768" | true
        "0"      | true
        "32767"  | true
        "32768"  | false
        "3276a8" | false
    }

    def "isInteger"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isInteger().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isInteger", title)
        }

        where:
        title         | result
        null          | false
        ""            | false
        "abc"         | false
        "-2147483649" | false
        "-2147483648" | true
        "0"           | true
        "2147483647"  | true
        "2147483648"  | false
        "21474836a48" | false
    }

    def "isLong"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isLong().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isLong", title)
        }

        where:
        title                  | result
        null                   | false
        ""                     | false
        "abc"                  | false
        "-9223372036854775809" | false
        "-9223372036854775808" | true
        "0"                    | true
        "9223372036854775807"  | true
        "9223372036854775808"  | false
        "9223372036854a775808" | false
    }

    def "isFloat"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isFloat().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isFloat", title)
        }

        where:
        title     | result
        null      | false
        ""        | false
        "abc"     | false
        "-19.95a" | false
        "-19.95"  | true
        "0"       | true
        "19.95"   | true
        "19.a95"  | false
    }

    def "isDouble"() {
        setup:
        def validator = FluentValidator.of(Car).string("title").isDouble().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == FluentValidator.Result.success(car)
        } else {
            assert answer == FluentValidator.Result.failure(car, "title", "isDouble", title)
        }

        where:
        title     | result
        null      | false
        ""        | false
        "abc"     | false
        "-19.95a" | false
        "-19.95"  | true
        "0"       | true
        "19.95"   | true
        "19.a95"  | false
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
