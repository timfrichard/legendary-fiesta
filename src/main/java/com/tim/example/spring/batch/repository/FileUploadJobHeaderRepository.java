package com.tim.example.spring.batch.repository;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileUploadJobHeaderRepository extends
        CrudRepository<FileUploadJobHeader, Long> {

    public List<FileUploadJobHeader> findByJobExecutionIdIsNull();

    @Query("FROM FileUploadJobHeader WHERE jobExecutionId IS NULL AND fileUploadDateTime = " +
            "(SELECT MIN(fileUploadDateTime) FROM FileUploadJobHeader)")
    public FileUploadJobHeader getReadyToProcessFile();
}
