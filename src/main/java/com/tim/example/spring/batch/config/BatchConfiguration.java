package com.tim.example.spring.batch.config;

import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.processors.item.listeners.JobCompletionNotificationListener;
import com.tim.example.spring.batch.processors.item.processor.TasBetcItemProcessor;
import com.tim.example.spring.batch.processors.item.writer.TasBetcItemWriter;
import com.tim.example.spring.batch.repository.TasBetcRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final int chunkSize;

    private final String[] fileHeaders;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final TasBetcRepository tasBetcRepository;

    @Autowired
    public BatchConfiguration(
            final @Value("${spring.batch.chunkSize}") int chunkSize,
            final @Value("${file.csv.headers:}") String[] fileHeaders, final JobBuilderFactory jobBuilderFactory,
            final StepBuilderFactory stepBuilderFactory,
            final TasBetcRepository tasBetcRepository, TasBetcItemWriter tasBetcItemWriter) {
        this.chunkSize = chunkSize;
        this.fileHeaders = fileHeaders;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tasBetcRepository = tasBetcRepository;
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
    public Job importTasBetcJob(final JobCompletionNotificationListener listener, final Step stepTasBetcWriter) {
        return jobBuilderFactory.get("importTasBetcJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepTasBetcWriter)
                .end()
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
     * The FlatFileItemReader will read the data from the file and pass it on to the next item.  The CSV file
     * which is being read is comma delimited.  There are two lines at the top of the file which will be skipped
     * down in the linemapper code.  The feature for comments should already work by using the # pound sign.
     *
     * @return FlatFileItemReader
     */
    @Bean
    public FlatFileItemReader<TasBetc> reader() {

        FlatFileItemReader<TasBetc> reader = new FlatFileItemReaderBuilder<TasBetc>()
                .name("tasbetcItemReader")
                /* A blank resource so there are no exceptions when the service comes up */
                .resource(new ClassPathResource("blank_all_tas_betc.csv"))
                /* Creation of the Line Mapper to handle file reading see methods below */
                .lineMapper(createLineMapper())
                /* Build the FlatFileItemReader */
                .build();
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(2);

        return reader;
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
     * @param taskExecutor
     * @return Step
     */
    @Bean
    public Step writeTasBetcStep(final TaskExecutor taskExecutor) {

        /* Using Spring JPA Repository to write the data via the save method */
        RepositoryItemWriter writer = new RepositoryItemWriter();
        writer.setRepository(tasBetcRepository);
        writer.setMethodName("save");

        return stepBuilderFactory.get("stepTasBetcWriter")
                /* Chunk value and this is from a property in the yml file */
                .<TasBetc, TasBetc>chunk(chunkSize)
                /* Reader from above FlatFileItemReader */
                .reader(reader())
                /* The item processor hooked in just in case any massaging of the data is needed before saving */
                .processor(tasBetcItemProcessor())
                /* setting the spring data repo as the writer */
                .writer(writer)
                /* setting the async task executor for speed. */
                .taskExecutor(taskExecutor)
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
