package com.tim.example.spring.batch;

import com.tim.example.spring.batch.config.BatchConfiguration;
import com.tim.example.spring.batch.config.QuartzConfig;
import com.tim.example.spring.batch.config.TasBetcFileProcessorConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({BatchConfiguration.class, QuartzConfig.class, TasBetcFileProcessorConfiguration.class})
public class TestSpringBatchConfiguration {
}
