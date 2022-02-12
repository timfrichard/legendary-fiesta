package com.tim.example.spring.batch.items.listeners;

import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.service.FileUploadJobHeaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class JobCompletionListener extends JobExecutionListenerSupport {

    private static final String QRY_GET_STATUS_EXIT_MSG = "SELECT READ_COUNT, EXIT_CODE, STATUS FROM " +
            "GINV_BATCH.BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID="; //BATCH_STEP_EXECUTION

    private final String postgresqlSchemaPrefix;

    private final FileUploadJobHeaderService fileUploadJobHeaderService;

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionListener(
            final String postgresqlSchemaPrefix,
            final FileUploadJobHeaderService fileUploadJobHeaderService,
            final JdbcTemplate jdbcTemplate) {
        this.postgresqlSchemaPrefix = postgresqlSchemaPrefix;
        this.fileUploadJobHeaderService = fileUploadJobHeaderService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info("JOB FINISHED Sync Data of the Header Table");
        /* Job Execution ID */
        Long executionJobId = jobExecution.getJobId();
        FileUploadJobHeader fileUploadJobHeader = loadFileUploadJobHeader(
                Long.valueOf(jobExecution.getJobParameters().getString(Constants.PARAMETERS_JOB_HEADER_ID))
                , executionJobId);

        if (fileUploadJobHeader != null) {
            /* Find the status of the Spring Batch Job */
            jdbcTemplate.query(QRY_GET_STATUS_EXIT_MSG
                            + executionJobId,
                    (rs, row) -> FileUploadJobHeader.builder().readCount(rs.getInt(1))
                            .exitCode(rs.getString(2)).status(rs.getString(3)).build()
            ).forEach(transferFileUploadJobHeader -> { /* There should only be one */
                Integer readCount = transferFileUploadJobHeader.getReadCount();
                log.info("ReadCount: " + readCount);
                fileUploadJobHeader.setReadCount(readCount);

                String exitCode = transferFileUploadJobHeader.getExitCode();
                log.info("ExitCode: " + exitCode);
                fileUploadJobHeader.setExitCode(exitCode);

                String status = transferFileUploadJobHeader.getStatus();
                log.info("Status: " + status);
                fileUploadJobHeader.setStatus(status);
            });

            fileUploadJobHeaderService.saveFileUploadJobHeader(fileUploadJobHeader);
        }
    }

    private FileUploadJobHeader loadFileUploadJobHeader(Long fileUploadJobHeaderId, Long jobExecutionId) {

        FileUploadJobHeader fileUploadJobHeader = null;
        if (fileUploadJobHeaderId != null) {
            fileUploadJobHeader = fileUploadJobHeaderService
                    .findById(fileUploadJobHeaderId);
            fileUploadJobHeader.setJobExecutionId(jobExecutionId);
        }

        return fileUploadJobHeader;
    }
}
