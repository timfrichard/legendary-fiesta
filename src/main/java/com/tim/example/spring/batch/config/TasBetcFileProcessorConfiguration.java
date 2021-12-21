package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.items.listeners.ItemReadListenerFailureLogger;
import com.tim.example.spring.batch.items.listeners.JobCompletionNotificationListener;
import com.tim.example.spring.batch.items.processor.TasBetcItemProcessor;
import com.tim.example.spring.batch.items.reader.TasBetcFlatFileReader;
import com.tim.example.spring.batch.items.writer.TasBetcItemWriter;
import com.tim.example.spring.batch.model.entities.TasBetc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class TasBetcFileProcessorConfiguration {

    private final int chunkSize;

    private final String[] fileHeaders;

    private final ItemReadListenerFailureLogger itemReadListenerFailureLogger;

    private final JobBuilderFactory jobBuilderFactory;

    private final Integer skipLimit;

    private final StepBuilderFactory stepBuilderFactory;

    private final TasBetcItemWriter tasBetcItemWriter;

    public TasBetcFileProcessorConfiguration(@Value("${spring.batch.chunkSize}") final int chunkSize,
                                             @Value("${file.csv.headers:}") final String[] fileHeaders,
                                             final ItemReadListenerFailureLogger itemReadListenerFailureLogger,
                                             final JobBuilderFactory jobBuilderFactory,
                                             @Value("${spring.batch.skip-limit}") Integer skipLimit,
                                             final StepBuilderFactory stepBuilderFactory,
                                             final TasBetcItemWriter tasBetcItemWriter) {
        this.chunkSize = chunkSize;
        this.fileHeaders = fileHeaders;
        this.itemReadListenerFailureLogger = itemReadListenerFailureLogger;
        this.jobBuilderFactory = jobBuilderFactory;
        this.skipLimit = skipLimit;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tasBetcItemWriter = tasBetcItemWriter;
    }

    @Bean
    public Job jobFileUpload(final JobCompletionNotificationListener jobCompletionNotificationListener) {
        return jobBuilderFactory.get("jobFileUploadProcessing")
                .incrementer(new RunIdIncrementer())
                /* Prevents the job from restarting if somehow a crash happens during the running */
                /* I don't know if we should set this! */
                .preventRestart()
                /* This listener does the work after the job has completed */
                .listener(jobCompletionNotificationListener)
                .start(stepFileUpload())
                .build();
    }

    @Bean
    public Step stepFileUpload() {
        return stepBuilderFactory.get("stepFileUpload")
                .<TasBetc, TasBetc>chunk(chunkSize)
                /* Synchronized Async FlatFileItemReader */
                .reader(synchronizedItemStreamReader())
                .listener(itemReadListenerFailureLogger)
                /* The item processor hooked in just in case any massaging of the data is needed before saving */
                .processor(tasBetcItemProcessor())
                /* setting the spring data repo as the writer */
                .writer(tasBetcItemWriter)
                /* Adding fault tolerance in order to configure skipping */
                .faultTolerant()
                /* Skip Limit setting */
                .skipLimit(skipLimit)
                /* The specific exceptions we are skipping */
                .skip(FlatFileFormatException.class)
                .skip(ParseException.class)
                /* setting the async task executor for speed. */
                /* NOTE: It matters where in this step builder to place this executor. */
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TasBetcFlatFileReader tasBetcFlatFileItemReader() {
        return new TasBetcFlatFileReader(fileHeaders);
    }

    /**
     * This Bean instantiation is for the Item Processor just in case you need to do some massaging of data in
     * the staged tables.
     *
     * @return TasBetcItemProcessor
     */
    @Bean
    public TasBetcItemProcessor tasBetcItemProcessor() {
        return new TasBetcItemProcessor();
    }

    @Bean
    public SynchronizedItemStreamReader<TasBetc> synchronizedItemStreamReader(){
        return new SynchronizedItemStreamReaderBuilder<TasBetc>()
                .delegate(tasBetcFlatFileItemReader())
                .build();
    }

    /**
     * This is a TaskExecutor Bean it good for async chunk reading really speeds up the reading of the file.  Also,
     * this doesn't guarantee order which we have to make sure our process does need to depend on the order of the
     * CSV file.
     *
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("tas_betc_reader_task_executor");
    }

}
