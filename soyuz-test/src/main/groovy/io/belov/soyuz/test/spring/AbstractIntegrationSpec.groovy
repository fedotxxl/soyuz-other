package io.belov.soyuz.test.spring

import org.springframework.context.annotation.ImportResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [Configuration])
@ActiveProfiles("test")
abstract class AbstractIntegrationSpec extends Specification {

    static {
        //activate test profile
        System.setProperty("spring.profiles.active", "test")
    }

    @ImportResource(["classpath:applicationContext.test.xml"])
    static class Configuration {
    }

}