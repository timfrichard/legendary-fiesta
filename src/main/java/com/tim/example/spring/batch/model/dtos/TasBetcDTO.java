package com.tim.example.spring.batch.model.dtos;

import com.tim.example.spring.batch.model.constraints.EmptyAllowedMaxMinSize;
import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasBetcDTO {

    @EmptyAllowedMaxMinSize(max = 2, min = 2, label = "Component SP")
    private String componentTasSP;

    @EmptyAllowedMaxMinSize(max = 3, min = 3, label = "Component ATA")
    private String componentTasATA;

    @NotNull(message = "AID can't be null.")
    @EmptyAllowedMaxMinSize(allowEmpty = false, max = 3, min = 3, label = "Component AID")
    private String componentTasAID;

    @EmptyAllowedMaxMinSize(max = 4, min = 4, label = "Component BPOA")
    private String componentTasBPOA;

    @EmptyAllowedMaxMinSize(max = 4, min = 4, label = "Component EPOA")
    private String componentTasEPOA;

    @EmptyAllowedMaxMinSize(max = 1, min = 1, label = "Component Availability Type")
    private String componentTasA;

    @NotNull(message = "Main Account can't be null.")
    @EmptyAllowedMaxMinSize(allowEmpty = false, max = 4, min = 4, label = "Component Main")
    private String componentTasMain;

    @NotNull(message = "Sub Account can't be null.")
    @EmptyAllowedMaxMinSize(allowEmpty = false, max = 3, min = 3, label = "Component Sub Account")
    private String componentTasSub;

    @EmptyAllowedMaxMinSize(max = 2, min = 2, label = "Component Admin Bureau")
    private String adminBureau;

    @EmptyAllowedMaxMinSize(max = 27, label = "String TAS")
    private String gwaTas;

    private String gwaTasName;

    private String agencyName;

    @NotNull(message = "BETC can't be null.")
    @EmptyAllowedMaxMinSize(allowEmpty = false, max = 8, min = 2, label = "BETC")
    private String betc;

    private String betcName;

    private String effectiveDate;

    private String suspendDate;

    @NotNull(message = "Is Credit can't be null.")
    @EmptyAllowedMaxMinSize(allowEmpty = false, max = 1, min = 1, label = "IsCredit is a min/max of 1")
    private String credit;

    private String adjustmentBetc;

    private String starTas;

    private String starDeptReg;

    private String starDeptXfr;

    private String starMainAccount;

    private String transactionType;

    private String accountType;

    private String accountTypeDescription;

    private String fundType;

    private String fundTypeDescription;

    private LocalDateTime processDateTime;

    private FileUploadJobHeader fileUploadJobHeader;

    private String blankComma;

}
