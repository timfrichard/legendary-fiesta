package com.tim.example.spring.batch.repository;

import com.tim.example.spring.batch.model.entities.JobHeader;
import org.springframework.data.repository.CrudRepository;

public interface JobHeaderRepository extends CrudRepository<JobHeader, Long> {
}
