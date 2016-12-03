package io.belov.soyuz.test.spring

import org.springframework.context.annotation.ImportResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
/**
 * Created by fbelov on 01.02.16.
 */
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(classes = [Configuration])
@ActiveProfiles("test")
abstract class AbstractIntegrationTransactionalSpec extends Specification {

    static {
        //activate test profile
        System.setProperty("spring.profiles.active", "test")
    }

    @ImportResource(["classpath:applicationContext.test.xml"])
    static class Configuration {
    }
}