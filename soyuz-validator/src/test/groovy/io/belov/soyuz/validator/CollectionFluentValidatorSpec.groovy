package io.belov.soyuz.validator

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Specification

class CollectionFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").notEmpty().b().build()

        then:
        assert validator.validate(new Team()) == FluentValidator.Result.failure("members", "notEmpty", null)
        assert validator.validate(new Team(members: [] as Set)) == FluentValidator.Result.failure("members", "notEmpty", [] as Set)
        assert validator.validate(new Team(members: [new Member(name: "filya")])) == FluentValidator.Result.success()
    }

    def "min"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").min(2).b().build()

        then:
        assert validator.validate(new Team()) == FluentValidator.Result.failure("members", "min", null)
        assert validator.validate(new Team(members: [new Member(name: "filya")] as Set)) == FluentValidator.Result.failure("members", "min", [new Member(name: "filya")] as Set)
        assert validator.validate(new Team(members: [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set)) == FluentValidator.Result.success()
    }

    def "max"() {
        when:
        def validator = FluentValidator.of(Team).collection("members").max(2).b().build()

        then:
        assert validator.validate(new Team()) == FluentValidator.Result.success()
        assert validator.validate(new Team(members: [new Member(name: "filya")] as Set)) == FluentValidator.Result.success()

        when:
        def members = [new Member(name: "filya"), new Member(name: "l80"), new Member(name: "sheishin")] as Set

        then:
        assert validator.validate(new Team(members: members)) == FluentValidator.Result.failure("members", "max", members)
    }

    def "itemValidator"() {
        when:
        def memberValidator = FluentValidator.of(Member).string("name").notEmpty().b().build()
        def validator = FluentValidator.of(Team).collection("members").itemValidator(memberValidator).b().build()

        then:
        assert validator.validate(new Team()) == FluentValidator.Result.success()
        assert validator.validate(new Team(members: [new Member(name: "filya")] as Set)) == FluentValidator.Result.success()

        when:
        def member = new Member(name: "")

        then:
        assert validator.validate(new Team(members: [member] as Set)) == FluentValidator.Result.failure("members.name", "notEmpty", "") //todo think about value?
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
