package com.tim.example.spring.batch.processors.item.listeners;

import com.tim.example.spring.batch.model.entities.TasBetc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			jdbcTemplate.query("SELECT GWA_TAS, BETC FROM TAS_BETC WHERE FK_JOB_EXECUTION_ID=" + jobExecution.getJobId(),
				(rs, row) -> TasBetc.builder().gwaTas(rs.getString(1))
						.betc(rs.getString(2)).build()
			).forEach(tasbetc -> log.info("Found <" + tasbetc + "> in the database."));
		}
	}
}
