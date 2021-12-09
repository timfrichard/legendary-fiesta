package com.tim.example.spring.batch.controller;

import com.tim.example.spring.batch.model.entities.JobHeader;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.repository.JobHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("launchFileUpload")
public class LaunchFileUploadController {

    private final Job importTasBetcJob;

    private final JobHeaderRepository jobHeaderRepository;

    private final JobLauncher jobLauncher;

    private final JobRepository jobRepository;

    private final FlatFileItemReader<TasBetc> tasbetcItemReader;

    @Autowired
    public LaunchFileUploadController(JobLauncher jobLauncher, Job importTasBetcJob,
                                      JobHeaderRepository jobHeaderRepository, JobRepository jobRepository,
                                      FlatFileItemReader tasbetcItemReader) {
        this.jobLauncher = jobLauncher;
        this.importTasBetcJob = importTasBetcJob;
        this.jobHeaderRepository = jobHeaderRepository;
        this.jobRepository = jobRepository;
        this.tasbetcItemReader = tasbetcItemReader;
    }

    @GetMapping
    public void startBatchFileUpload(@RequestParam("tasbetFile") MultipartFile tasbetcFile) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {

        /* Creating a JobHeader and placing it in the Jobs parameters for use in the items writer */
        final JobHeader jobHeader = jobHeaderRepository.save(JobHeader.builder().build());

        final JobParameters params = new JobParametersBuilder()
                .addString("jobStartValue", String.valueOf(System.currentTimeMillis()))
                .addLong("jobHeaderId", jobHeader.getId())
                .toJobParameters();

        log.info("Setting File here");
        tasbetcItemReader.setResource(tasbetcFile.getResource());

        log.info("Starting the batch job");
        final JobExecution jobExecution = jobLauncher.run(importTasBetcJob, params);

        log.info("Job Id: " + jobExecution.getJobId());
        jobHeader.setJobExecutionId(jobExecution.getJobId());
        jobHeaderRepository.save(jobHeader);
    }

}
