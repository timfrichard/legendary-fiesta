package com.tim.example.spring.batch.service;

import com.tim.example.spring.batch.model.entities.ProcessingError;
import com.tim.example.spring.batch.repository.ProcessingErrorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessingErrorService {

    private final ProcessingErrorRepository processingErrorRepository;

    public ProcessingErrorService(ProcessingErrorRepository processingErrorRepository) {
        this.processingErrorRepository = processingErrorRepository;
    }

    public ProcessingError saveProcessingError(ProcessingError processingError){

        return processingErrorRepository.save(processingError);
    }
}
