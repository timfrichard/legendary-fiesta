package com.tim.example.spring.batch.items.listeners;

import com.tim.example.spring.batch.model.dtos.TasBetcDTO;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.model.entities.ProcessingError;
import com.tim.example.spring.batch.service.ProcessingErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import javax.transaction.Transactional;

@Slf4j
public class ItemReaderErrorListener implements ItemReadListener<TasBetcDTO>{

    private final Long fileUploadJobHeaderId;

    private final ProcessingErrorService processingErrorService;

    public ItemReaderErrorListener(final Long fileUploadJobHeaderId,
                                   final ProcessingErrorService processingErrorService) {
        this.fileUploadJobHeaderId = fileUploadJobHeaderId;
        this.processingErrorService = processingErrorService;
    }

    @Override
    public void beforeRead() {
        // no op
    }

    @Override
    public void afterRead(TasBetcDTO tasBetc) {
        // no op
    }

    /**
     * The transactional annotation is needed as a REQUIRES_NEW.
     * See:  https://docs.spring.io/spring-batch/docs/current/reference/html/common-patterns.html#loggingItemProcessingAndFailures
     * @param e Exception
     */
    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void onReadError(Exception e) {

        final ProcessingError processingError = ProcessingError.builder().fileUploadJobHeader(FileUploadJobHeader
                .builder().jobHeaderId(fileUploadJobHeaderId).build()).build();

        if (e instanceof IncorrectTokenCountException) {
            IncorrectTokenCountException incorrectTokenCountException = (IncorrectTokenCountException) e;
            String errorMessage = incorrectTokenCountException.getMessage();
            log.error("FlatFormatException: ", errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        } else if (e instanceof FlatFileParseException) {
            FlatFileParseException flatFileParseException = (FlatFileParseException) e;
            String errorMessage = String.format("Line: %o Input: %s", flatFileParseException.getLineNumber(),
                    flatFileParseException.getInput()).toString();
            log.error(errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        } else {
            String errorMessage = e.getMessage();
            log.error("Encountered error on read DUMMY!", errorMessage);
            processingError.setProcessingErrorDescription(errorMessage);
        }

        processingErrorService.saveProcessingError(processingError);
    }

}
