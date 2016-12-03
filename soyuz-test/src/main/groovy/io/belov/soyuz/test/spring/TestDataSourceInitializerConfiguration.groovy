/*
 * TestDataSourceInitializerConfiguration
 * Copyright (c) 2012 Cybervision. All rights reserved.
 */
package io.belov.soyuz.test.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.init.DataSourceInitializer

import javax.sql.DataSource

class TestDataSourceInitializerConfiguration {

    @Value("#{'\${test.sql.initFiles:}'.split(', ')}")
    private List<String> initFiles = []

    @Autowired
    private DataSource dataSource

    @Bean
    public TestService testService() {
        return new TestService()
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final TestService testService) {
        initFiles.each {
            if (it) testService.executeClassPathSql(it)
        }

        return new DataSourceInitializer(dataSource: dataSource)
    }

}
