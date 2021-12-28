package com.tim.example.spring.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {


    @Bean
    @Primary
    public StepScope stepScope() {
        final StepScope stepScope = new StepScope();
        stepScope.setAutoProxy(false);
        return stepScope;
    }
}
