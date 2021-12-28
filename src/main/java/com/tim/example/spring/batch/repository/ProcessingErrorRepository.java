package com.tim.example.spring.batch.repository;

import com.tim.example.spring.batch.model.entities.ProcessingError;
import org.springframework.data.repository.CrudRepository;

public interface ProcessingErrorRepository extends CrudRepository<ProcessingError, Long> {
}
