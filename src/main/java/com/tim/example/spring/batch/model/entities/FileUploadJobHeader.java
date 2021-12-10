package com.tim.example.spring.batch.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FILE_UPLOAD_JOB_HEADER")
public class FileUploadJobHeader extends BaseEntity{

    @Column(name = "EXIT_CODE")
    private String exitCode;

    @Column(name = "FK_JOB_EXECUTION_ID")
    private Long jobExecutionId;

    @Column(name = "FILE_UPLOAD_JOB_HEADER_ID", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_header_sequence_gen")
    @SequenceGenerator(allocationSize = 10, name = "job_header_sequence_gen", sequenceName = "FILE_UPLOAD_JOB_HEADER_SEQ")
    private Long jobHeaderId;

    @Column(name = "READ_COUNT")
    private Integer readCount;

    @Column(name = "STATUS")
    private String status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fileUploadJobHeader")
    private Set<TasBetc> tasbetcs;

    @Override
    public Long getId() {
        return jobHeaderId;
    }
}
