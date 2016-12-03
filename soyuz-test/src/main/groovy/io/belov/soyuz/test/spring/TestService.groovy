/*
 * TestService
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.test.spring
import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.TimeUnit

@Slf4j
class TestService {

    @Autowired
    private JdbcTemplate template

    @Transactional
    void executeClassPathSql(String fileName) {
        log.debug("Executing SQL script from class path resource [${fileName}]")

        def stopwatch = Stopwatch.createStarted();
        template.execute(new ClassPathResource(fileName).inputStream.text)

        log.info("Done executing SQL script from class path resource [${fileName}] in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)} ms")
    }

}
