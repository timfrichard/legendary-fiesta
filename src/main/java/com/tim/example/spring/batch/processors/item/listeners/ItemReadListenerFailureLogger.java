package com.tim.example.spring.batch.processors.item.listeners;

import com.tim.example.spring.batch.model.entities.TasBetc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemReadListenerFailureLogger implements ItemReadListener<TasBetc> {

    @Override
    public void beforeRead() {
        // no op
    }

    @Override
    public void afterRead(TasBetc tasBetc) {
        // no op
    }

    @Override
    public void onReadError(Exception e) {

        if (e instanceof IncorrectTokenCountException) {
            IncorrectTokenCountException incorrectTokenCountException = (IncorrectTokenCountException) e;
            log.error("FlatFormatException: ", incorrectTokenCountException.getMessage());
        } else if (e instanceof FlatFileParseException) {
            FlatFileParseException flatFileParseException = (FlatFileParseException) e;
            log.error(String.format("Line: %o Input: %s", flatFileParseException.getLineNumber(),
                    flatFileParseException.getInput()).toString());
        } else {
            log.error("Encountered error on read DUMMY!", e.getMessage());
        }
    }
}
