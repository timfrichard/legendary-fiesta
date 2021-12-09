package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.processors.item.listeners.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    @Autowired
    public BatchConfiguration(
            final JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    /**
     * This method is the instantiation of the Spring Batch Job in which the Step(s) is passed via
     * method arguments along with a Listener and this listener checks the database for saved items.
     *
     * @param listener
     * @param stepTasBetcWriter
     * @return Job
     */
    @Bean
    public Job batchFileUploadJob(final JobCompletionNotificationListener listener, final Step stepTasBetcWriter) {
        return jobBuilderFactory.get("importTasBetcJob")
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .listener(listener)
                .flow(stepTasBetcWriter)
                .end()
                .build();
    }

}
