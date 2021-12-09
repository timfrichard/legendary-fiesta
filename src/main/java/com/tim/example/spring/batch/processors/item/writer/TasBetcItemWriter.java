package com.tim.example.spring.batch.processors.item.writer;

import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.repository.JobHeaderRepository;
import com.tim.example.spring.batch.repository.TasBetcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DON'T USE
 * Only use if really needed just an example and is not being used in the current configuration.
 */
@Component
@Slf4j
public class TasBetcItemWriter implements ItemWriter<TasBetc> {

    private StepExecution stepExecution;

    private final JobHeaderRepository jobHeaderRepository;

    private final TasBetcRepository tasBetcRepository;

    @Autowired
    public TasBetcItemWriter(JobHeaderRepository jobHeaderRepository, TasBetcRepository tasBetcRepository) {
        this.jobHeaderRepository = jobHeaderRepository;
        this.tasBetcRepository = tasBetcRepository;
    }

    @Override
    public void write(List<? extends TasBetc> tasBetcs) throws Exception {

        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        log.info("Job Id: " + this.stepExecution.getJobExecutionId());

        tasBetcs.forEach(tasBetc -> {
            tasBetc.setJobHeader(jobHeaderRepository.findById(
                            this.stepExecution.getJobExecution().getJobParameters().getLong("jobHeaderId"))
                    .get());
        });

        tasBetcRepository.saveAll(tasBetcs);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
