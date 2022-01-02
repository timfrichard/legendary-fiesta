package com.tim.example.spring.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties
@EnableScheduling
public class BatchConfiguration {

/* Not sure if we need this leaving it here and commented out in case we have StepScope issues */
//    @Bean
//    @Primary
//    public StepScope stepScope() {
//        final StepScope stepScope = new StepScope();
//        stepScope.setAutoProxy(false);
//        return stepScope;
//    }
}
