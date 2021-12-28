package com.tim.example.spring.batch.items.listeners;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.model.entities.ProcessingError;
import com.tim.example.spring.batch.service.ProcessingErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import javax.transaction.Transactional;

@Slf4j
public class CollectiveItemErrorListener extends ItemListenerSupport {

    private final Long fileUploadJobHeaderId;

    private final ProcessingErrorService processingErrorService;

    public CollectiveItemErrorListener(final Long fileUploadJobHeaderId,
                                       final ProcessingErrorService processingErrorService) {
        this.fileUploadJobHeaderId = fileUploadJobHeaderId;
        this.processingErrorService = processingErrorService;
    }

    /**
     * The transactional annotation is needed as a REQUIRES_NEW.
     * See:  https://docs.spring.io/spring-batch/docs/current/reference/html/common-patterns.html#loggingItemProcessingAndFailures
     * @param exception Exception
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void onReadError(Exception exception) {
        final ProcessingError processingError = ProcessingError.builder().fileUploadJobHeader(FileUploadJobHeader
                .builder().jobHeaderId(fileUploadJobHeaderId).build()).build();

        if (exception instanceof IncorrectTokenCountException) {
            IncorrectTokenCountException incorrectTokenCountException = (IncorrectTokenCountException) exception;
            String errorMessage = incorrectTokenCountException.getMessage();
            log.error("FlatFormatException: ", errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        } else if (exception instanceof FlatFileParseException) {
            FlatFileParseException flatFileParseException = (FlatFileParseException) exception;
            String errorMessage = String.format("Line: %o Input: %s", flatFileParseException.getLineNumber(),
                    flatFileParseException.getInput()).toString();
            log.error(errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        } else {
            String errorMessage = exception.getMessage();
            log.error("Encountered error on read DUMMY!", errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        }

        processingErrorService.saveProcessingError(processingError);
    }
}
