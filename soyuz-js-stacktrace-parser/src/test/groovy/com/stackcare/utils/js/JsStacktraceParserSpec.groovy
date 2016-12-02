package com.stackcare.utils.js

import io.belov.soyuz.js.JsStacktrace
import io.belov.soyuz.js.JsStacktraceEntry
import io.belov.soyuz.js.JsStacktraceParser
import io.belov.soyuz.utils.ClassPathFile
import spock.lang.Specification

/**
 * Created by fbelov on 20.04.15.
 */
class JsStacktraceParserSpec extends Specification {

    private JsStacktraceParser parser = new JsStacktraceParser()

    def "should parse stacktrace"() {
        setup:
        def parsed = ClassPathFile.asStream(sourcePath + ".parsed.txt").text
        def expected = parsed.split("\n").collect {
            def columns = it.split("\\|")
            if (columns.length == 1) {
                return new JsStacktraceEntry(columns[0])
            } else if (columns.length == 4) {
                return new JsStacktraceEntry(columns[0], getStringOr(columns[1]), getStringOr(columns[2]), getIntOr(columns[3]))
            } else if (columns.length == 5) {
                return new JsStacktraceEntry(columns[0], getStringOr(columns[1]), getStringOr(columns[2]), getIntOr(columns[3]), getIntOr(columns[4]))
            } else {
                throw new IllegalStateException()
            }
        }

        expect:
        assert parser.parse(ClassPathFile.asStream(sourcePath + ".txt").text) == new JsStacktrace(expected)

        where:
        sourcePath << [
                "js.st.3",
                "js.st.4",
                "js.st.5",
                "js.stacktrace.1",
                "js.stacktrace.2",
                "js.stacktrace.chrome-extension",
                "js.stacktrace.chrome-extension.2"
        ]
    }

    private getStringOr(String value) {
        return value ?: null
    }

    private getIntOr(String value) {
        return (value) ? value.toInteger() : -1
    }

}
