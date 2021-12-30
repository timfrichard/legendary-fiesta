package com.tim.example.spring.batch.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROCESSING_ERROR")
public class ProcessingError {

    @ManyToOne(optional = false)
    @JoinColumn(name = "FK_FILE_UPLOAD_JOB_HEADER_ID", nullable = false)
    private FileUploadJobHeader fileUploadJobHeader;

    @Column(name = "ERROR_DESCRIPTION")
    private String processingErrorDescription;

    @Column(name = "PROCESSING_ERROR_ID", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "processing_error_sequence_gen")
    @SequenceGenerator(allocationSize = 10, name = "processing_error_sequence_gen", sequenceName = "PROCESSING_ERROR_SEQ")
    private Long processingErrorId;

    @Column(name = "STEP_TYPE_ERROR", length = 30)
    @Enumerated(EnumType.STRING)
    private StepTypeError stepTypeError;

    @Column(name = "VERSION_ID")
    @Version
    private Long version;

}
