package com.tim.example.spring.batch.items.processor;

import com.tim.example.spring.batch.model.entities.TasBetc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TasBetcItemProcessor implements ItemProcessor<TasBetc, TasBetc> {

    private StepExecution stepExecution;

    @Override
    public TasBetc process(final TasBetc tasBetc) throws Exception {

//        Instant now = Instant.now();
//        log.info("Processing (" + tasBetc + ") at the following time: (" + now + ")");


//        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
//        stepExecution.getJobExecution().getJobParameters().getString("jobStartValue")
//        tasBetc.setProcessDateTime(LocalDateTime.now());
        log.info("Job Id: " + this.stepExecution.getJobExecutionId());

        return tasBetc;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
