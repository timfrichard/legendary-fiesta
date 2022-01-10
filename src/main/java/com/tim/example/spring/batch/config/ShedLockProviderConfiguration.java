package com.tim.example.spring.batch.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ShedLockProviderConfiguration {

    private static final String SHEDLOCK_TABLE_NAME = "SHEDLOCK";

    @Bean
    public LockProvider lockProvider(final DataSource dataSource,
                                     final @Value("${spring.jpa.properties.hibernate.default_schema:}")
                                             String postgresqlSchemaPrefix) {

        StringBuffer sb = new StringBuffer();
        if (!StringUtils.isEmpty(postgresqlSchemaPrefix)) {
            sb.append(postgresqlSchemaPrefix).append(".").append(SHEDLOCK_TABLE_NAME);
        } else {
            sb.append(SHEDLOCK_TABLE_NAME);
        }

        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withTableName(sb.toString())
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .build()
        );
    }
}
