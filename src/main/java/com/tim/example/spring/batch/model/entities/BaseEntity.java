package com.tim.example.spring.batch.model.entities;

import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class BaseEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean persisted = false;

    @Override
    public boolean isNew() {
        return !persisted;
    }

    @PostPersist
    @PostLoad
    protected void setPersisted() {
        this.persisted = true;
    }
}
