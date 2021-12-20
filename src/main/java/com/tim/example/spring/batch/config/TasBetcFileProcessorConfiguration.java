package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.items.reader.TasBetcFlatFileReader;
import com.tim.example.spring.batch.items.writer.TasBetcItemWriter;
import com.tim.example.spring.batch.model.entities.TasBetc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TasBetcFileProcessorConfiguration {

    private final int chunkSize;

    private final String[] fileHeaders;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final TasBetcItemWriter tasBetcItemWriter;

    public TasBetcFileProcessorConfiguration(@Value("${spring.batch.chunkSize}") final int chunkSize,
                                             @Value("${file.csv.headers:}") final String[] fileHeaders,
                                             final JobBuilderFactory jobBuilderFactory,
                                             final StepBuilderFactory stepBuilderFactory,
                                             final TasBetcItemWriter tasBetcItemWriter) {
        this.chunkSize = chunkSize;
        this.fileHeaders = fileHeaders;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tasBetcItemWriter = tasBetcItemWriter;
    }

    @Bean
    public Job jobFileUpload() {
        return jobBuilderFactory.get("jobFileUploadProcessing")
                .start(stepFileUpload())
                .build();
    }

    @Bean
    public Step stepFileUpload() {
        return stepBuilderFactory.get("stepFileUpload")
                .<TasBetc, TasBetc>chunk(chunkSize)
                .reader(tasBetcFlatFileItemReader())
                .writer(tasBetcItemWriter)
                .build();
    }

    @Bean
    public TasBetcFlatFileReader tasBetcFlatFileItemReader() {
        return new TasBetcFlatFileReader(fileHeaders);
    }

}
