package io.belov.soyuz.validator

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class CollectionFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").notEmpty().b().build()

        then:
        assert validator.validate(team) == result(team)

        where:
        team                                           | result
        new Team()                                     | { t -> FluentValidator.Result.failure(t, Err.field("members").code("notEmpty").build()) }
        new Team(members: [] as Set)                   | { t -> FluentValidator.Result.failure(t, Err.field("members").code("notEmpty").value([] as Set).build()) }
        new Team(members: [new Member(name: "filya")]) | { t -> FluentValidator.Result.success(t) }
    }

    def "greaterThan"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").greaterThan(2).b().build()

        then:
        assert validator.validate(team) == result(team)

        where:
        team                                                                                                         | result
        new Team()                                                                                                   | { t -> FluentValidator.Result.failure(t, Err.field("members").code("greaterThan").params(["criterion": 2]).build()) }
        new Team(members: [new Member(name: "filya")] as Set)                                                        | { t -> FluentValidator.Result.failure(t, Err.field("members").code("greaterThan").params(["criterion": 2]).value([new Member(name: "filya")] as Set).build()) }
        new Team(members: [new Member(name: "filya"), new Member(name: "l80")] as Set)                               | { t -> FluentValidator.Result.failure(t, Err.field("members").code("greaterThan").params(["criterion": 2]).value([new Member(name: "filya"), new Member(name: "l80")] as Set).build()) }
        new Team(members: [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set) | { t -> FluentValidator.Result.success(t) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").greaterOrEqual(2).b().build()

        then:
        assert validator.validate(team) == result(team)

        where:
        team                                                                                                         | result
        new Team()                                                                                                   | { t -> FluentValidator.Result.failure(t, Err.field("members").code("greaterOrEqual").params(["criterion": 2]).build()) }
        new Team(members: [new Member(name: "filya")] as Set)                                                        | { t -> FluentValidator.Result.failure(t, Err.field("members").code("greaterOrEqual").params(["criterion": 2]).value([new Member(name: "filya")] as Set).build()) }
        new Team(members: [new Member(name: "filya"), new Member(name: "l80")] as Set)                               | { t -> FluentValidator.Result.success(t) }
        new Team(members: [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set) | { t -> FluentValidator.Result.success(t) }
    }

    def "lessThan"() {
        setup:
        def validator = FluentValidator.of(Team).collection("members").lessThan(2).b().build()

        when:
        def team = new Team(members: members)

        then:
        assert validator.validate(team) == result(team, members)

        where:
        members                                                                                   | result
        null                                                                                      | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya")] as Set                                                        | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya"), new Member(name: "l80")] as Set                               | { t, m -> FluentValidator.Result.failure(t, Err.field("members").code("lessThan").params(["criterion": 2]).value(m).build()) }
        [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set | { t, m -> FluentValidator.Result.failure(t, Err.field("members").code("lessThan").params(["criterion": 2]).value(m).build()) }
    }

    def "lessOrEqual"() {
        setup:
        def validator = FluentValidator.of(Team).collection("members").lessOrEqual(2).b().build()

        when:
        def team = new Team(members: members)

        then:
        assert validator.validate(team) == result(team, members)

        where:
        members                                                                                   | result
        null                                                                                      | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya")] as Set                                                        | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya"), new Member(name: "l80")] as Set                               | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set | { t, m -> FluentValidator.Result.failure(t, Err.field("members").code("lessOrEqual").params(["criterion": 2]).value(m).build()) }
    }

    def "itemValidator"() {
        setup:
        def memberValidator = FluentValidator.of(Member).string("name").notEmpty().b().build()
        def validator = FluentValidator.of(Team).collection("members").itemValidator(memberValidator).b().build()

        when:
        def team = new Team(members: members)

        then:
        assert validator.validate(team) == result(team, members)

        where:
        members                            | result
        null                               | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "filya")] as Set | { t, m -> FluentValidator.Result.success(t) }
        [new Member(name: "")] as Set      | { t, m -> FluentValidator.Result.failure(t, Err.field("members.0.name").code("notEmpty").value("").build()) } //todo think about value?
    }

    @EqualsAndHashCode
    @ToString
    static class Team {
        String title
        Set<Member> members;
    }

    @EqualsAndHashCode
    @ToString
    static class Member {
        String name;
    }
}
