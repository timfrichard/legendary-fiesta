package com.tim.example.spring.batch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class BatchScheduledProcessorConfiguration {

//    @Bean
//    @Primary
//    public StepScope stepScope() {
//        final StepScope stepScope = new StepScope();
//        stepScope.setAutoProxy(false);
//        return stepScope;
//    }
}
