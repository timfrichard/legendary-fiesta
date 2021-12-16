package com.tim.example.spring.batch.controller;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.repository.FileUploadJobHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
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

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("launchFileUpload")
public class LaunchFileUploadController {

    private final Job importTasBetcJob;

    private final FileUploadJobHeaderRepository fileUploadJobHeaderRepository;

    private final JobLauncher jobLauncher;

    private final JobRepository jobRepository;

    private final FlatFileItemReader<TasBetc> tasbetcItemReader;

    private final SimpleJobOperator jobOperator;

    @Autowired
    public LaunchFileUploadController(JobLauncher jobLauncher, Job importTasBetcJob,
                                      FileUploadJobHeaderRepository fileUploadJobHeaderRepository, JobRepository jobRepository,
                                      FlatFileItemReader tasbetcItemReader, SimpleJobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.importTasBetcJob = importTasBetcJob;
        this.fileUploadJobHeaderRepository = fileUploadJobHeaderRepository;
        this.jobRepository = jobRepository;
        this.tasbetcItemReader = tasbetcItemReader;
        this.jobOperator = jobOperator;
    }

    @GetMapping
    public void startBatchFileUpload(@RequestParam("tasbetcFile") MultipartFile tasbetcFile) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException, JobInstanceAlreadyExistsException, NoSuchJobException {

        /* Creating a JobHeader and placing it in the Jobs parameters for use in the items writer */
        final com.tim.example.spring.batch.model.entities.FileUploadJobHeader fileUploadJobHeader =
                fileUploadJobHeaderRepository.save(FileUploadJobHeader.builder().build());

        final JobParameters params = new JobParametersBuilder()
                .addString("jobStartValue", String.valueOf(System.currentTimeMillis()))
                .addLong("jobHeaderId", fileUploadJobHeader.getId())
                .toJobParameters();

        log.info("Setting File here");
        tasbetcItemReader.setResource(tasbetcFile.getResource());

        Set<String> jobNames = jobOperator.getJobNames();
        log.info("Starting the batch job");
        Long jobId = jobOperator.start("importTasBetcJob", stringifyParams(params));
//        final JobExecution jobExecution = jobLauncher.run(importTasBetcJob, params);

        log.info("Job Id: " + jobId);
        fileUploadJobHeader.setJobExecutionId(jobId);
        this.fileUploadJobHeaderRepository.save(fileUploadJobHeader);
    }

    private String stringifyParams(JobParameters params) {

        String stringParams = params.toProperties().toString();
        stringParams = StringUtils.remove(stringParams, "{");
        stringParams = StringUtils.remove(stringParams, "}");

        log.info("String Parameters: ", stringParams);

        return stringParams;
    }

}
