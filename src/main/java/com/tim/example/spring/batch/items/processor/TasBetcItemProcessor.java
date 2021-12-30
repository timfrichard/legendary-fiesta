package com.tim.example.spring.batch.items.processor;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.model.mapper.TasBetcMapper;
import com.tim.example.spring.batch.service.TasBetcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class TasBetcItemProcessor implements ItemProcessor<TasBetcDTO, TasBetc> {

    private final TasBetcService tasBetcService;

    private final ValidatorFactory validator;

    public TasBetcItemProcessor(TasBetcService tasBetcService, ValidatorFactory validator) {
        this.tasBetcService = tasBetcService;
        this.validator = validator;
    }

    @Override
    public TasBetc process(TasBetcDTO tasBetcDTO) throws Exception {

        log.info("Processing TasBetcDTO to TasBetc: " + tasBetcDTO);
        /* Code to first remove whitespaces from String fields and second to validate conformity to JSR 380 */
        final Set<ConstraintViolation<TasBetcDTO>> results = validator.getValidator()
                .validate(tasBetcService.removeWhiteSpaces(tasBetcService.removeUnprintableCharacters(tasBetcDTO)));
        /* If there is a failure with validation then we should throw the constraint exceptions */
        /* NOTE: this does count against the Skip Limit configured in the Batch Step */
        if(!results.isEmpty()){
            throw new ConstraintViolationException("There are validation exceptions.", results);
        }
        /* Use MapStruct to convert the DTO into a mapped Entity */
        TasBetc tasBetc = TasBetcMapper.tasBetcMapper.dtoTasBetcToTasBetc(tasBetcDTO);

        return tasBetc;
    }

}
