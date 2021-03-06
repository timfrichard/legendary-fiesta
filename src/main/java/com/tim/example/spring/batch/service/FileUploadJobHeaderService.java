package com.tim.example.spring.batch.service;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.repository.FileUploadJobHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileUploadJobHeaderService {

    private final FileUploadJobHeaderRepository fileUploadJobHeaderRepository;

    public FileUploadJobHeaderService(FileUploadJobHeaderRepository fileUploadJobHeaderRepository) {
        this.fileUploadJobHeaderRepository = fileUploadJobHeaderRepository;
    }

    public FileUploadJobHeader getReadyToProcessFile() {
        return fileUploadJobHeaderRepository.getReadyToProcessFile();
    }

    public FileUploadJobHeader saveFileUploadJobHeader(final FileUploadJobHeader fileUploadJobHeader){
        log.info("Saving File Upload Header");
        return fileUploadJobHeaderRepository.save(fileUploadJobHeader);
    }

    public FileUploadJobHeader findById(Long jobHeaderId) {
        return fileUploadJobHeaderRepository.findById(jobHeaderId).get();
    }
}
