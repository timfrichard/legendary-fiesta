package com.tim.example.spring.batch.processors.item.listeners;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.repository.FileUploadJobHeaderRepository;
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

    private final FileUploadJobHeaderRepository fileUploadJobHeaderRepository;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(
            FileUploadJobHeaderRepository fileUploadJobHeaderRepository,
            JdbcTemplate jdbcTemplate) {
        this.fileUploadJobHeaderRepository = fileUploadJobHeaderRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            log.info("!!! JOB FINISHED! Time to verify the results");

            Long jobHeaderId = Long.valueOf(jobExecution.getJobParameters().getString("jobHeaderId"));
            /* This would be null when it is not run by the jobLauncher */
            if (jobHeaderId != null) {
                FileUploadJobHeader fileUploadJobHeader = fileUploadJobHeaderRepository
                        .findById(jobHeaderId).get();

                jdbcTemplate.query(QRY_GET_STATUS_EXIT_MSG
                                + jobExecution.getJobId(),
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

                fileUploadJobHeaderRepository.save(fileUploadJobHeader);
            }
        }
    }
}
