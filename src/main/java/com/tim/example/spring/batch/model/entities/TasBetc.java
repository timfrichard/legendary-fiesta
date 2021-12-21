package com.tim.example.spring.batch.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tas_betc")
public class TasBetc {

    @Column(name = "ID_NUMBER", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tas_betc_sequence_gen")
    @SequenceGenerator(allocationSize = 10, name = "tas_betc_sequence_gen", sequenceName = "TAS_BETC_SEQ")
    private Long id;

    @Column(name = "CMPNT_TAS_SP")
    private String componentTasSP;

    @Column(name = "CMPNT_TAS_ATA")
    private String componentTasATA;

    @Column(name = "CMPNT_TAS_AID")
    private String componentTasAID;

    @Column(name = "CMPNT_TAS_BPOA")
    private String componentTasBPOA;

    @Column(name = "CMPNT_TAS_EPOA")
    private String componentTasEPOA;

    @Column(name = "CMPNT_TAS_A")
    private String componentTasA;

    @Column(name = "CMPNT_TAS_MAIN")
    private String componentTasMain;

    @Column(name = "CMPNT_TAS_SUB")
    private String componentTasSub;

    @Column(name = "ADMIN_BUR")
    private String adminBureau;

    @Column(name = "GWA_TAS")
    private String gwaTas;

    @Column(name = "GWA_TAS_NAME")
    private String gwaTasName;

    @Column(name = "AGENCY_NAME")
    private String agencyName;

    @Column(name = "BETC")
    private String betc;

    @Column(name = "BETC_NAME")
    private String betcName;

    @Column(name = "EFF_DATE")
    private String effectiveDate;

    @Column(name = "SUSPEND_DATE")
    private String suspendDate;

    @Column(name = "CREDIT")
    private String credit;

    @Column(name = "ADJ_BETC")
    private String adjustmentBetc;

    @Column(name = "STAR_TAS")
    private String starTas;

    @Column(name = "STAR_DEPT_REG")
    private String starDeptReg;

    @Column(name = "STAR_DEPT_XFR")
    private String starDeptXfr;

    @Column(name = "STAR_MAIN_ACCT")
    private String starMainAccount;

    @Column(name = "TXN_TYPE")
    private String transactionType;

    @Column(name = "ACCT_TYPE")
    private String accountType;

    @Column(name = "ACCT_TYPE_DESCRIPTION")
    private String accountTypeDescription;

    @Column(name = "FUND_TYPE")
    private String fundType;

    @Column(name = "FUND_TYPE_DESCRIPTION")
    private String fundTypeDescription;

    @Column(name = "PROCESS_DATETIME")
    private LocalDateTime processDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FK_FILE_UPLOAD_JOB_HEADER_ID", nullable = false)
    private FileUploadJobHeader fileUploadJobHeader;

    @Transient
    private String blankComma;
}
