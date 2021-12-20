package com.tim.example.spring.batch.controller;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import com.tim.example.spring.batch.repository.FileUploadJobHeaderRepository;
import com.tim.example.spring.batch.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("launchFileUpload")
public class LaunchFileUploadController {

    private final FileUploadJobHeaderRepository fileUploadJobHeaderRepository;

    private final StorageService storageService;

    public LaunchFileUploadController(final FileUploadJobHeaderRepository fileUploadJobHeaderRepository,
                                      final StorageService storageService) {
        this.fileUploadJobHeaderRepository = fileUploadJobHeaderRepository;
        this.storageService = storageService;
    }

    @GetMapping
    public FileUploadJobHeader uploadProcessingFile(@RequestParam("tasbetcFile") MultipartFile tasbetcFile) {

        FileUploadJobHeader fileUploadJobHeader = FileUploadJobHeader.builder()
                .fileName(tasbetcFile.getOriginalFilename())
                .fileUploadDateTime(LocalDateTime.now()).build();

        try {
            storageService.store(tasbetcFile);
        } finally {
            fileUploadJobHeader = fileUploadJobHeaderRepository.save(fileUploadJobHeader);
        }

        return fileUploadJobHeader;
    }

}
