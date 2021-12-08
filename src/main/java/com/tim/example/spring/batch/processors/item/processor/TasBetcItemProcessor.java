package com.tim.example.spring.batch.processors.item.processor;

import com.tim.example.spring.batch.model.entities.TasBetc;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import java.time.Instant;

@Slf4j
public class TasBetcItemProcessor implements ItemProcessor<TasBetc, TasBetc> {

    private StepExecution stepExecution;

    @Override
    public TasBetc process(final TasBetc tasBetc) throws Exception {

        Instant now = Instant.now();
        log.info("Processing (" + tasBetc + ") at the following time: (" + now + ")");
        tasBetc.setProcessDateTime(now);

        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        log.info("Job Id: " + this.stepExecution.getJobExecutionId());
        tasBetc.setJobExecutionId(this.stepExecution.getJobExecutionId());
        return tasBetc;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
