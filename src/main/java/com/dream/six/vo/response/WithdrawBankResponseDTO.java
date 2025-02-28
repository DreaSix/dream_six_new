package com.dream.six.vo.response;

import lombok.Data;
import java.util.UUID;

@Data
public class WithdrawBankResponseDTO {
    private UUID id;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String ifscCode;
}
