package com.tim.example.spring.batch.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Set;

@Builder
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Table(name = "JOB_HEADER")
public class JobHeader extends BaseEntity{

    @Column(name = "FK_JOB_EXECUTION_ID")
    private Long jobExecutionId;

    @Column(name = "JOB_HEADER_ID", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_header_sequence_gen")
    @SequenceGenerator(allocationSize = 10, name = "job_header_sequence_gen", sequenceName = "JOB_HEADER_SEQ")
    private Long jobHeaderId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobHeader")
    private Set<TasBetc> tasbetcs;

    @Override
    public Long getId() {
        return jobHeaderId;
    }
}
