package io.belov.soyuz.validator

import io.belov.soyuz.validator.message.FvMessageResolverFromPropertiesFile
import spock.lang.Specification

/**
 * Created by fbelov on 07.06.16.
 */
class FvMessageResolverFromPropertiesFileSpec extends Specification {

    def "should transform error messages"() {
        when:
        def root = new Object()
        def is = FluentValidator.class.getClassLoader().getResourceAsStream("fv.properties")
        def propertyNameResolver = new FvMessageResolverFromPropertiesFile.BasePropertyNameResolver()
        def messageResolver = new FvMessageResolverFromPropertiesFile(is, "UTF-8", propertyNameResolver)

        then:
        assert messageResolver.getMessage(root, new FluentValidator.Error("notNull", null)) == "Object should not be null"
        assert messageResolver.getMessage(root, new FluentValidator.Error("title", "empty", null)) == "Title should not be empty"
        assert messageResolver.getMessage(root, new FluentValidator.Error("company", "notNull", null)) == "Object should not be null"
        assert messageResolver.getMessage(root, new FluentValidator.Error("employee.secondName", "invalid", "filya")) == "Invalid second name (filya)"
        assert messageResolver.getMessage(root, new FluentValidator.Error("employee.job", "invalid", "developer", ["filya"] as Object[])) == "Invalid job value for user filya (\"developer\")"
    }

    def "should thrown an exception"() {
        setup:
        def root = new Object()
        def is = FluentValidator.class.getClassLoader().getResourceAsStream("fv.properties")
        def propertyNameResolver = new FvMessageResolverFromPropertiesFile.BasePropertyNameResolver()
        def messageResolver = new FvMessageResolverFromPropertiesFile(is, "UTF-8", propertyNameResolver)

        when:
        messageResolver.getMessage(root, new FluentValidator.Error("unknownErrorCode", null))

        then:
        thrown(IllegalStateException)
    }
}
