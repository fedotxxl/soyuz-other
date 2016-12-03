package io.belov.soyuz.text.md

import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by fbelov on 04.11.15.
 */
@ContextConfiguration(value = [
        "classpath*:applicationContext.s.text.xml",
        "classpath:applicationContext.s.text.md.xml",
        "classpath*:applicationContext.test.properties.xml"
])
abstract class AbstractSpringSpec extends Specification {

}
