package com.tim.example.spring.batch.items.reader;

import com.tim.example.spring.batch.AbstractSpringBatchTest;
import com.tim.example.spring.batch.items.Constants;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.repository.FileUploadJobHeaderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestTasBetcFlatFileReader extends AbstractSpringBatchTest {

    @Autowired
    private FileUploadJobHeaderRepository fileUploadJobHeaderRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @DisplayName("Test Skip Count in Reader")
    public void whenFileReaderExceedsSkipCount() {

        /* Faking out as if a file was just uploaded */
        String fileName = "error_tas_betc.csv";
        FileUploadJobHeader fileUploadJobHeader = saveFileUploadJobHeader(fileName);

        /* WHEN */
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.STEP_FILE_UPLOAD,
                defaultJobParameters(fileName, fileUploadJobHeader.getJobHeaderId()));
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualExitStatus = jobExecution.getExitStatus();

        /* THEN */
        assertThat(actualStepExecutions.size(), is(1));
        assertThat(actualExitStatus.getExitCode(), is(ExitStatus.FAILED.getExitCode()));
        actualStepExecutions.forEach(stepExecution -> {
            /* Get the correct ReadCount amounts */
            assertThat(stepExecution.getReadCount(), is(7));
            assertThat(stepExecution.getSkipCount(), is(2));
            assertThat(stepExecution.getRollbackCount(), is(1));
        });
    }

    @Test
    @DisplayName("Test File Upload Read Count")
    public void whenFileUploadReaderExecute_thenSuccess() {

        /* Faking out as if a file was just uploaded */
        String fileName = "min_all_tas_betc.csv";
        FileUploadJobHeader fileUploadJobHeader = saveFileUploadJobHeader(fileName);

        /* WHEN */
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.STEP_FILE_UPLOAD,
                defaultJobParameters(fileName, fileUploadJobHeader.getJobHeaderId()));
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualExitStatus = jobExecution.getExitStatus();

        /* THEN */
        assertThat(actualStepExecutions.size(), is(1));
        assertThat(actualExitStatus.getExitCode(), is(ExitStatus.COMPLETED.getExitCode()));
        actualStepExecutions.forEach(stepExecution -> {
            /* Get the correct ReadCount amounts */
            assertThat(stepExecution.getReadCount(), is(11));
        });
    }

    private JobParameters defaultJobParameters(final String fileName, final Long jobHeaderId) {

        JobParameters paramsBuilder = new JobParametersBuilder()
                .addString(Constants.PARAMETERS_JOB_START_VALUE, String.valueOf(System.currentTimeMillis()))
                .addLong(Constants.PARAMETERS_JOB_HEADER_ID, jobHeaderId)
                .addString(Constants.PARAMETERS_TAS_BETC_FILE_NAME, fileName)
                .toJobParameters();

        return paramsBuilder;
    }

    private FileUploadJobHeader saveFileUploadJobHeader(final String fileName) {
        FileUploadJobHeader fileUploadJobHeader = FileUploadJobHeader.builder()
                .fileName(fileName)
                .fileUploadDateTime(LocalDateTime.now()).build();
        fileUploadJobHeader = fileUploadJobHeaderRepository.save(fileUploadJobHeader);
        return fileUploadJobHeader;
    }
}
