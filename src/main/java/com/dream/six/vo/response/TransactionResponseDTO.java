package com.dream.six.vo.response;

import com.dream.six.enums.Status;
import com.dream.six.enums.TransactionType;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class TransactionResponseDTO {
    private UUID id;
    private Double amount;
    private String utr;
    private PaymentResponseDTO paymentResponseDTO;
    private TransactionType transactionType;
    private Status approvalStatus;
    private String userName;
    private String name;
    private String transactionImage;
    private Timestamp createdAt;
    private String accountNumber;
    private String accountHolderName;
    private String ifscCode;
    private UserResponseVO userResponseVO;

}
