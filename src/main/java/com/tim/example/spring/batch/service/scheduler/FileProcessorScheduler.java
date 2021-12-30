package com.tim.example.spring.batch.service.scheduler;

import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.items.reader.TasBetcFlatFileReader;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.service.FileUploadJobHeaderService;
import com.tim.example.spring.batch.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileProcessorScheduler {

    private final FileUploadJobHeaderService fileUploadJobHeaderService;

    private final Job jobFileUploadProcessing;

    private final JobLauncher jobLauncher;

    private final StorageService storageService;

    private final TasBetcFlatFileReader tasBetcFlatFileReader;

    public FileProcessorScheduler(final FileUploadJobHeaderService fileUploadJobHeaderService,
                                  final Job jobFileUploadProcessing, final JobLauncher jobLauncher,
                                  final StorageService storageService,
                                  final TasBetcFlatFileReader tasBetcFlatFileReader) {
        this.fileUploadJobHeaderService = fileUploadJobHeaderService;
        this.jobFileUploadProcessing = jobFileUploadProcessing;
        this.jobLauncher = jobLauncher;
        this.storageService = storageService;
        this.tasBetcFlatFileReader = tasBetcFlatFileReader;
    }

    @Scheduled(cron="*/10 * * * * *")
    public void launchFileUploadJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {
        log.info("Start - launchFileUploadJob()");

        FileUploadJobHeader fileUploadJobHeader = fileUploadJobHeaderService.getReadyToProcessFile();
        if (fileUploadJobHeader != null) {
            final JobParameters params = new JobParametersBuilder()
                    .addString(Constants.PARAMETERS_JOB_START_VALUE, String.valueOf(System.currentTimeMillis()))
                    .addLong(Constants.PARAMETERS_JOB_HEADER_ID, fileUploadJobHeader.getId())
                    .addString(Constants.PARAMETERS_TAS_BETC_FILE_NAME, fileUploadJobHeader.getFileName())
                    .toJobParameters();

            log.info("Starting the batch job");
            final JobExecution jobExecution = jobLauncher.run(jobFileUploadProcessing, params);
        } else {
            log.info("There are no files waiting to be processed.");
        }
    }
}
