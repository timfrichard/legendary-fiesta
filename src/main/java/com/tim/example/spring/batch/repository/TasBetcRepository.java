package com.tim.example.spring.batch.repository;

import com.tim.example.spring.batch.model.entities.TasBetc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasBetcRepository extends CrudRepository<TasBetc, Long> {
}
