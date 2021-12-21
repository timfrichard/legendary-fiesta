package com.tim.example.spring.batch.items.listeners;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.service.FileUploadJobHeaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final String QRY_GET_STATUS_EXIT_MSG = "SELECT READ_COUNT, EXIT_CODE, STATUS FROM " +
            "BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID=";

    private final FileUploadJobHeaderService fileUploadJobHeaderService;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(
            final FileUploadJobHeaderService fileUploadJobHeaderService,
            final JdbcTemplate jdbcTemplate) {
        this.fileUploadJobHeaderService = fileUploadJobHeaderService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("!!! JOB FINISHED! Time to verify the results");

            Long jobHeaderId = Long.valueOf(jobExecution.getJobParameters().getString("jobHeaderId"));
            /* This would be null when it is not run by the jobLauncher */
            if (jobHeaderId != null) {
                Long executionJobId = jobExecution.getJobId();
                FileUploadJobHeader fileUploadJobHeader = fileUploadJobHeaderService
                        .findById(jobHeaderId);
                fileUploadJobHeader.setJobExecutionId(executionJobId);

                jdbcTemplate.query(QRY_GET_STATUS_EXIT_MSG
                                + executionJobId,
                        (rs, row) -> FileUploadJobHeader.builder().readCount(rs.getInt(1))
                                .exitCode(rs.getString(2)).status(rs.getString(3)).build()
                ).forEach(fileUploadJobHeader1 -> {
                    log.info("ReadCount: " + fileUploadJobHeader1.getReadCount());
                    log.info("ExitCode: " + fileUploadJobHeader1.getExitCode());
                    log.info("Status: " + fileUploadJobHeader1.getStatus());
                    fileUploadJobHeader.setReadCount(fileUploadJobHeader1.getReadCount());
                    fileUploadJobHeader.setExitCode(fileUploadJobHeader1.getExitCode());
                    fileUploadJobHeader.setStatus(fileUploadJobHeader1.getStatus());
                });

                fileUploadJobHeaderService.saveFileUploadJobHeader(fileUploadJobHeader);
            }
        }
    }
}
