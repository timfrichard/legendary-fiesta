package com.tim.example.spring.batch.repository;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;

public interface FileUploadJobHeaderRepository extends
        CrudRepository<FileUploadJobHeader, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("FROM FileUploadJobHeader FUJH WHERE FUJH.jobExecutionId IS NULL AND fileUploadDateTime = " +
            "(SELECT MIN(fileUploadDateTime) FROM FileUploadJobHeader WHERE jobExecutionId IS NULL)")
    public FileUploadJobHeader getReadyToProcessFile();
}
