package com.tim.example.spring.batch.repository;

import org.springframework.data.repository.CrudRepository;

public interface FileUploadJobHeaderRepository extends
        CrudRepository<com.tim.example.spring.batch.model.entities.FileUploadJobHeader, Long> {
}
