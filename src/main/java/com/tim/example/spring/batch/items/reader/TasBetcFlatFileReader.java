package com.tim.example.spring.batch.items.reader;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;

@Slf4j
public class TasBetcFlatFileReader extends FlatFileItemReader<TasBetcDTO> {

    private final String[] fileHeaders;

    private final int linesToSkip;

    public TasBetcFlatFileReader(final String[] fileHeaders, int linesToSkip){

        this.fileHeaders = fileHeaders;
        this.linesToSkip = linesToSkip;
        setName("tasbetcItemReader");
        /* Creation of the Line Mapper to handle file reading see methods below */
        setLineMapper(createLineMapper());
        /* Set number of lines to skips. Use it if file has header rows. */
        setLinesToSkip(linesToSkip);
    }

    /**
     * Creating a LineMapper for the FlatFileItemReader and each line will represent a TasBetc.
     *
     * @return LineMapper
     */
    private LineMapper<TasBetcDTO> createLineMapper() {
        DefaultLineMapper<TasBetcDTO> tasBetcDefaultLineMapper = new DefaultLineMapper<>();
        LineTokenizer lineTokenizer = createTasBetcLineTokenizer();
        tasBetcDefaultLineMapper.setLineTokenizer(lineTokenizer);
        FieldSetMapper<TasBetcDTO> fieldSetMapper = createTasBetcFieldSetMapper();
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
    private FieldSetMapper<TasBetcDTO> createTasBetcFieldSetMapper() {

        BeanWrapperFieldSetMapper<TasBetcDTO> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(TasBetcDTO.class);

        return beanWrapperFieldSetMapper;
    }

}
