package com.dream.six.vo.response;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentResponseDTO {
    private UUID id;
    private String paymentMethod;

    private String accountName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;

    private String upiId;
    private String upiPhone;
    private String qrCodeUrl;

    private Double amount;
    private String statusMessage;
}
