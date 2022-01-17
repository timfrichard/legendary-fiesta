package com.tim.example.spring.batch.quartz;

import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.items.reader.TasBetcFlatFileReader;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.service.FileUploadJobHeaderService;
import com.tim.example.spring.batch.service.storage.StorageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Slf4j
public class FileUploadQuartzJob extends QuartzJobBean {

    private String jobName;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobLocator jobLocator;

    @Autowired
    private FileUploadJobHeaderService fileUploadJobHeaderService;

    @Autowired
    private Job jobFileUploadProcessing;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TasBetcFlatFileReader tasBetcFlatFileReader;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("Start - launchFileUploadJob()");
            FileUploadJobHeader fileUploadJobHeader = fileUploadJobHeaderService.getReadyToProcessFile();
            if (fileUploadJobHeader != null) {
                Job job = jobLocator.getJob(jobName);
                JobParameters params = new JobParametersBuilder()
                        .addString(Constants.PARAMETERS_JOB_START_VALUE, String.valueOf(System.currentTimeMillis()))
                        .addString(Constants.PARAMETERS_JOB_START_VALUE, String.valueOf(System.currentTimeMillis()))
                        .addLong(Constants.PARAMETERS_JOB_HEADER_ID, fileUploadJobHeader.getId())
                        .addString(Constants.PARAMETERS_TAS_BETC_FILE_NAME, fileUploadJobHeader.getFileName())
                        .toJobParameters();

                log.info("Starting the batch job with Quartz");
                jobLauncher.run(job, params);
            } else {
                log.info("There are no files waiting to be processed.");
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
