package com.dream.six.vo.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateWithdrawRequestDTO {
    private UUID withdrawBankId;
    private Double amount;
}