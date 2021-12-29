package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.items.listeners.ItemReaderErrorListener;
import com.tim.example.spring.batch.items.listeners.JobCompletionListener;
import com.tim.example.spring.batch.items.processor.TasBetcItemProcessor;
import com.tim.example.spring.batch.items.reader.TasBetcFlatFileReader;
import com.tim.example.spring.batch.items.writer.TasBetcItemWriter;
import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.service.ProcessingErrorService;
import com.tim.example.spring.batch.service.storage.StorageService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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

import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;

@Configuration
public class TasBetcFileProcessorConfiguration {

    private final int chunkSize;

    private final String[] fileHeaders;

    private final JobBuilderFactory jobBuilderFactory;

    private final Integer skipLimit;

    private final StepBuilderFactory stepBuilderFactory;

    private final StorageService storageService;

    private final TasBetcItemWriter tasBetcItemWriter;

    private final ValidatorFactory validator;

    public TasBetcFileProcessorConfiguration(final @Value("${spring.batch.chunkSize}") int chunkSize,
                                             final @Value("${file.csv.headers:}") String[] fileHeaders,
                                             final JobBuilderFactory jobBuilderFactory,
                                             final @Value("${spring.batch.skip-limit}") Integer skipLimit,
                                             final StepBuilderFactory stepBuilderFactory,
                                             final StorageService storageService,
                                             final TasBetcItemWriter tasBetcItemWriter,
                                             final ValidatorFactory validator) {
        this.chunkSize = chunkSize;
        this.fileHeaders = fileHeaders;
        this.jobBuilderFactory = jobBuilderFactory;
        this.skipLimit = skipLimit;
        this.stepBuilderFactory = stepBuilderFactory;
        this.storageService = storageService;
        this.tasBetcItemWriter = tasBetcItemWriter;
        this.validator = validator;
    }

    @Bean
    public Job jobFileUpload(final JobCompletionListener jobCompletionListener,
                             final Step stepFileUpload) {
        return jobBuilderFactory.get(Constants.JOB_NAME)
                .incrementer(new RunIdIncrementer())
                /* Prevents the job from restarting if somehow a crash happens during the running */
                /* I don't know if we should set this! */
                .preventRestart()
                /* This listener does the work after the job has completed */
                .listener(jobCompletionListener)
                .start(stepFileUpload)
                .build();
    }

    @Bean
    public Step stepFileUpload(final SynchronizedItemStreamReader<TasBetcDTO> synchronizedItemStreamReader,
                               final ItemReaderErrorListener itemReaderErrorListener,
                               final TasBetcItemProcessor tasBetcItemProcessor) {
        return stepBuilderFactory.get(Constants.STEP_FILE_UPLOAD)
                .<TasBetcDTO, TasBetc>chunk(chunkSize)
                /* Synchronized Async FlatFileItemReader */
                .reader(synchronizedItemStreamReader)
                .listener(itemReaderErrorListener)
                /* The item processor hooked in just in case any massaging of the data is needed before saving */
                .processor(tasBetcItemProcessor)
                /* setting the spring data repo as the writer */
                .writer(tasBetcItemWriter)
                /* Adding fault tolerance in order to configure skipping */
                .faultTolerant()
                /* Skip Limit setting */
                .skipLimit(skipLimit)
                /* The specific exceptions we are skipping */
                .skip(FlatFileFormatException.class)
                .skip(ParseException.class)
                .skip(ConstraintViolationException.class)
                /* setting the async task executor for speed. */
                /* NOTE: It matters where in this step builder to place this executor. */
                /* NOTE II: Using this Executor derails the skip logic */
//                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public TasBetcFlatFileReader tasBetcFlatFileItemReader(
            final @Value("#{jobParameters['"+ Constants.PARAMETERS_TAS_BETC_FILE_NAME + "']}") String fileName,
            final @Value("${spring.batch.lines-to-skip}") int linesToSkip) {

        /* Instantiating the Flat File Reader */
        TasBetcFlatFileReader tasBetcFlatFileReader = new TasBetcFlatFileReader(fileHeaders, linesToSkip);
        /* Setting the file name which should have been set during launch */
        tasBetcFlatFileReader.setResource(storageService.loadAsResource(fileName));

        return tasBetcFlatFileReader;
    }

    /**
     * This Bean instantiation is for the Item Processor just in case you need to do some massaging of data in
     * the staged tables.
     *
     * @return TasBetcItemProcessor
     */
    @Bean
    public TasBetcItemProcessor tasBetcItemProcessor() {
        return new TasBetcItemProcessor(validator);
    }

    @Bean
    public SynchronizedItemStreamReader<TasBetcDTO> synchronizedItemStreamReader(final TasBetcFlatFileReader tasBetcFlatFileReader) {
        return new SynchronizedItemStreamReaderBuilder<TasBetcDTO>()
                .delegate(tasBetcFlatFileReader)
                .build();
    }

    /**
     * This is a TaskExecutor Bean it good for async chunk reading really speeds up the reading of the file.  Also,
     * this doesn't guarantee order which we have to make sure our process does need to depend on the order of the
     * CSV file.
     * <p>
     * I decided not to use this because it wasn't predictable when depending on fault tolerance and skip count.  Since
     * it runs asynchronous you may have failures in different threads therefore the count would be off.
     *
     * @return TaskExecutor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("tas_betc_reader_task_executor");
    }

    @Bean
    @StepScope
    public ItemReaderErrorListener itemReadListenerFailureLogger(
            final @Value("#{jobParameters['"+ Constants.PARAMETERS_JOB_HEADER_ID + "']}") Long fileUploadJobHeaderId,
            final ProcessingErrorService processingErrorService) {

        final ItemReaderErrorListener itemReaderErrorListener =
                new ItemReaderErrorListener(fileUploadJobHeaderId, processingErrorService);

        return itemReaderErrorListener;
    }
}
