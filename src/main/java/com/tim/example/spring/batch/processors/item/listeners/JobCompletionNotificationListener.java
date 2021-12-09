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
			"BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID=7";

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
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

			log.info("!!! JOB FINISHED! Time to verify the results");

			FileUploadJobHeader fileUploadJobHeader = fileUploadJobHeaderRepository
					.findById(jobExecution.getJobParameters().getLong("jobHeaderId")).get();

//			jdbcTemplate.query(QRY_GET_STATUS_EXIT_MSG
//			+ jobExecution.getJobParameters().getLong("jobHeaderId"),
//					(rs, row) -> fileUploadJobHeader.setExitCode(rs.getString(2)))
//					.forEach();



//			jdbcTemplate.query("SELECT GWA_TAS, BETC FROM TAS_BETC WHERE FK_FILE_UPLOAD_JOB_HEADER_ID="
//							+ jobExecution.getJobParameters().getLong("jobHeaderId"),
//				(rs, row) -> TasBetc.builder().gwaTas(rs.getString(1))
//						.betc(rs.getString(2)).build()
//			).forEach(tasbetc -> log.info("Found <" + tasbetc + "> in the database."));
		}
	}
}
