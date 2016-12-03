package io.belov.soyuz.text.md

import io.belov.soyuz.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by fbelov on 04.11.15.
 */
class MdProcessorSpec extends AbstractSpringSpec {

    @Autowired
    private MdProcessor processor

    def "should parse md file"() {
        setup:
        def fileProvider = TestUtils.getClassPathFileProvider("md-processor")
        def mdFile = fileProvider.get(fileName + ".md")
        def expectedFile = fileProvider.get(fileName + ".txt")

        when:
        def answer = processor.process(mdFile.text)

        then:
        assert answer.title == title
        assert answer.html.trim() == expectedFile.text.trim()
        assert answer.tags == (tags as Set)

        where:
        fileName | title                | tags
        "simple" | "Title from comment" | ["hello", "world", "urls", "something useful"]
    }

}
