package com.tim.example.spring.batch.processors.item.listeners;

import com.tim.example.spring.batch.MultipleJobsRunningException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class SingleInstanceListener implements JobExecutionListener {

    private final JobExplorer explorer;

    public SingleInstanceListener(JobExplorer explorer) {
        this.explorer = explorer;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        Set<JobExecution> executions = explorer.findRunningJobExecutions(jobName);
        if(executions.size() > 1) {
            try {
                throw new MultipleJobsRunningException(String.format("Job %s already running: ", jobName).toString());
            } catch (MultipleJobsRunningException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

}
