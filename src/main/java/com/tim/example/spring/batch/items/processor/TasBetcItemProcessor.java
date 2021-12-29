package com.tim.example.spring.batch.items.processor;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.model.mapper.TasBetcMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class TasBetcItemProcessor implements ItemProcessor<TasBetcDTO, TasBetc> {

    private StepExecution stepExecution;

    private final ValidatorFactory validator;

    public TasBetcItemProcessor(ValidatorFactory validator) {
        this.validator = validator;
    }

    @Override
    public TasBetc process(TasBetcDTO tasBetcDTO) throws Exception {
        final Set<ConstraintViolation<TasBetcDTO>> results = validator.getValidator().validate(tasBetcDTO);
        if(!results.isEmpty()){
            throw new ConstraintViolationException("There are validation exceptions.", results);
        }
        TasBetc tasBetc = TasBetcMapper.tasBetcMapper.dtoTasBetcToTasBetc(tasBetcDTO);
        return tasBetc;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
