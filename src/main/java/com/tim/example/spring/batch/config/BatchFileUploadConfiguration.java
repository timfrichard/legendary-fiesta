package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.processors.item.listeners.ItemReadListenerFailureLogger;
import com.tim.example.spring.batch.processors.item.processor.TasBetcItemProcessor;
import com.tim.example.spring.batch.processors.item.writer.TasBetcItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class BatchFileUploadConfiguration {

    private final int chunkSize;

    private final String[] fileHeaders;

    private final ItemReadListenerFailureLogger itemReadListenerFailureLogger;

    private final StepBuilderFactory stepBuilderFactory;

    private final TasBetcItemWriter tasBetcItemWriter;

    public BatchFileUploadConfiguration(final @Value("${spring.batch.chunkSize}") int chunkSize,
                                        final @Value("${file.csv.headers:}") String[] fileHeaders,
                                        ItemReadListenerFailureLogger itemReadListenerFailureLogger, StepBuilderFactory stepBuilderFactory,
                                        TasBetcItemWriter tasBetcItemWriter) {
        this.chunkSize = chunkSize;
        this.fileHeaders = fileHeaders;
        this.itemReadListenerFailureLogger = itemReadListenerFailureLogger;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tasBetcItemWriter = tasBetcItemWriter;
    }

    /**
     * The FlatFileItemReader will read the data from the file and pass it on to the next item.  The CSV file
     * which is being read is comma delimited.  There are two lines at the top of the file which will be skipped
     * down in the linemapper code.  The feature for comments should already work by using the # pound sign.
     *
     * @return
     */
    @Bean
    @StepScope
    public FlatFileItemReader<TasBetc> reader() {

        FlatFileItemReader<TasBetc> reader = new FlatFileItemReaderBuilder<TasBetc>()
                .name("tasbetcItemReader")
                /* A blank resource so there are no exceptions when the service comes up */
                /* Removing the blank resource set spring.batch.job.enabled=false in application.yml */
                //.resource(new ClassPathResource("blank_all_tas_betc.csv"))
                /* Creation of the Line Mapper to handle file reading see methods below */
                .lineMapper(createLineMapper())
                /* Build the FlatFileItemReader */
                .build();
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(2);

        return reader;
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<TasBetc> synchronizedItemStreamReader(){
        return new SynchronizedItemStreamReaderBuilder<TasBetc>()
                .delegate(reader())
                .build();
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

    /**
     * This method is used to setup the Batch writer to persist the data that is read from the file.  The writer
     * needs some type of reader and a processor to work.
     *
     * @return Step
     */
    @Bean
    public Step writeTasBetcStep() {

        return stepBuilderFactory.get("stepTasBetcWriter")
                /* Chunk value and this is from a property in the yml file */
                .<TasBetc, TasBetc>chunk(chunkSize)
                /* Reader from above FlatFileItemReader */
                .reader(synchronizedItemStreamReader())
                .listener(itemReadListenerFailureLogger)
                /* Adding fault tolerance in order to configure skipping */
                .faultTolerant()
                /* Skip Limit setting */
                .skipLimit(2)
                .skip(FlatFileFormatException.class)
                .skip(ParseException.class)
                /* The item processor hooked in just in case any massaging of the data is needed before saving */
                .processor(tasBetcItemProcessor())
                /* setting the spring data repo as the writer */
                .writer(tasBetcItemWriter)
                /* setting the async task executor for speed. */
                .taskExecutor(taskExecutor())
                /* Building the step */
                .build();
    }

    /**
     * Creating a LineMapper for the FlatFileItemReader and each line will represent a TasBetc.
     *
     * @return LineMapper
     */
    private LineMapper<TasBetc> createLineMapper() {
        DefaultLineMapper<TasBetc> tasBetcDefaultLineMapper = new DefaultLineMapper<>();
        LineTokenizer lineTokenizer = createTasBetcLineTokenizer();
        tasBetcDefaultLineMapper.setLineTokenizer(lineTokenizer);
        FieldSetMapper<TasBetc> fieldSetMapper = createTasBetcFieldSetMapper();
        tasBetcDefaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return tasBetcDefaultLineMapper;
    }

    /**
     * Creating a tokenizer for which a comma is the delimiter and the headers which are set in the yml properties
     * file that matches the private variable name in the bean entity.
     *
     * @return LineTokenizer
     */
    private LineTokenizer createTasBetcLineTokenizer() {

        DelimitedLineTokenizer fileLineTokenizer = new DelimitedLineTokenizer();
        fileLineTokenizer.setDelimiter(",");
        fileLineTokenizer.setNames(fileHeaders);
        return fileLineTokenizer;
    }

    /**
     * This method will set the fieldset mapper to use the TasBetc entity.
     *
     * @return FieldSetMapper
     */
    private FieldSetMapper<TasBetc> createTasBetcFieldSetMapper() {

        BeanWrapperFieldSetMapper<TasBetc> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TasBetc.class);

        return beanWrapperFieldSetMapper;
    }
}
