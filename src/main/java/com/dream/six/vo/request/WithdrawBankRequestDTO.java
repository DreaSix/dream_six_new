package com.dream.six.vo.request;

import lombok.Data;

@Data
public class WithdrawBankRequestDTO {
    private String bankName;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    }
