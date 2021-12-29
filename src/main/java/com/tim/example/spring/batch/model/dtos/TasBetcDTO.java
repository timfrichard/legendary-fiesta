package com.tim.example.spring.batch.model.dtos;

import com.tim.example.spring.batch.model.entities.FileUploadJobHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TasBetcDTO {

    @Size(max = 2, min = 2, message = "SP is a min/max of 2")
    private String componentTasSP;

    @Size(max = 3, min = 3, message = "ATA is a min/max of 3")
    private String componentTasATA;

    @NotNull(message = "AID can't be null.")
    @Size(max = 3, min = 3, message = "AID is a min/max of 3")
    private String componentTasAID;

    @Size(max = 4, min = 4, message = "BPOA is a min/max of 4")
    private String componentTasBPOA;

    @Size(max = 4, min = 4, message = "EPOA is a min/max of 4")
    private String componentTasEPOA;

    @Size(max = 1, min = 1, message = "Availability Type is a min/max of 1")
    private String componentTasA;

    @NotNull(message = "Main Account can't be null.")
    @Size(max = 4, min = 4, message = "Main is a min/max of 4")
    private String componentTasMain;

    @NotNull(message = "Sub Account can't be null.")
    @Size(max = 3, min = 3, message = "Sub Account is a min/max of 3")
    private String componentTasSub;

    @Size(max = 2, min = 2, message = "Admin Bureau is a min/max of 2")
    private String adminBureau;

    @Size(max = 27, message = "String TAS is a max of 27")
    private String gwaTas;

    private String gwaTasName;

    private String agencyName;

    @NotNull(message = "BETC can't be null.")
    @Size(max = 8, min = 2, message = "BETC is a min of 2 and max of 8")
    private String betc;

    private String betcName;

    private String effectiveDate;

    private String suspendDate;

    @NotNull(message = "Is Credit can't be null.")
    @Size(max = 1, min = 1, message = "IsCredit is a min/max of 1")
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
