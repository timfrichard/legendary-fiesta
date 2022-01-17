package com.tim.example.spring.batch.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file.upload.job")
public class FileUploadJobProperties {

    private Integer beginningLinesToSkip;

    private Integer chunkSize;

    private String cronSchedule;

    private String[] csvFileHeaders;

    private Path fileUploadRootDirectory;

    private Integer intervalInSeconds;

    private Integer skipLimit;
}