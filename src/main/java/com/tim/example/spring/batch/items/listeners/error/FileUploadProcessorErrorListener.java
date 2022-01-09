package com.tim.example.spring.batch.items.listeners.error;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.model.entities.ProcessingError;
import com.tim.example.spring.batch.model.entities.StepTypeError;
import com.tim.example.spring.batch.model.entities.TasBetc;
import com.tim.example.spring.batch.service.FileUploadJobHeaderService;
import com.tim.example.spring.batch.service.ProcessingErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Slf4j
public class FileUploadProcessorErrorListener implements ItemProcessListener<TasBetcDTO, TasBetc> {

    private final Long fileUploadJobHeaderId;

    private final FileUploadJobHeaderService fileUploadJobHeaderService;

    private final ProcessingErrorService processingErrorService;

    public FileUploadProcessorErrorListener(final Long fileUploadJobHeaderId,
                                            final FileUploadJobHeaderService fileUploadJobHeaderService,
                                            final ProcessingErrorService processingErrorService) {
        this.fileUploadJobHeaderId = fileUploadJobHeaderId;
        this.fileUploadJobHeaderService = fileUploadJobHeaderService;
        this.processingErrorService = processingErrorService;
    }

    @Override
    public void beforeProcess(TasBetcDTO tasBetcDTO) {
        //no op
    }

    @Override
    public void afterProcess(TasBetcDTO tasBetcDTO, TasBetc tasBetc) {
        //no op
    }

    /**
     * The transactional annotation is needed as a REQUIRES_NEW.
     * See:  https://docs.spring.io/spring-batch/docs/current/reference/html/common-patterns.html#loggingItemProcessingAndFailures
     *
     * @param tasBetcDTO
     * @param exception
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void onProcessError(TasBetcDTO tasBetcDTO, Exception exception) {

        final ProcessingError processingError = ProcessingError.builder().fileUploadJobHeader(FileUploadJobHeader
                        .builder().jobHeaderId(fileUploadJobHeaderId).build()).stepTypeError(StepTypeError.FILEUPLOADPROCESSOR)
                .build();

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            final Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) constraintViolationException)
                    .getConstraintViolations();
            StringBuffer sb = new StringBuffer();
            for (final ConstraintViolation<?> violation : constraintViolations) {
                sb.append(violation.getMessage() + " ");
            }
            String errorMessage = sb.toString();
            log.error(errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        } else {
            String errorMessage = exception.getMessage();
            log.error("Encountered error on read.", errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        }

        processingErrorService.saveProcessingError(processingError);
    }
}
