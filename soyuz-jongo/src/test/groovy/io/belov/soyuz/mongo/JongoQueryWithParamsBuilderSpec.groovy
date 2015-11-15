package io.belov.soyuz.mongo

import spock.lang.Specification

/**
 * Created by fbelov on 15.11.15.
 */
class JongoQueryWithParamsBuilderSpec extends Specification {

    def "should build queries with fields"() {
        setup:
        def column = new Object()

        when:
        def query = JongoQueryWithParams
                .builder()
                .field("pId", 1)
                .field("when", "before")
                .field("file", null)
                .field("line", 10)
                .field("column", column)
                .build()

        def params = [1, "before", null, 10, column]

        then:
        assert query.getQ() == "{pId: #, when: #, file: #, line: #, column: #}"
        assert query.getParams().toList() == params
    }

    def "should build queries with operators"() {
        when:
        def query = JongoQueryWithParams
                .builder()
                .field("pId", 1)
                .fieldWithOperator("message", "{\$in: #}", "hello world")
                .field("file", null)
                .build()

        def params = [1, "hello world", null]

        then:
        assert query.getQ() == "{pId: #, message: {\$in: #}, file: #}"
        assert query.getParams().toList() == params
    }

}
