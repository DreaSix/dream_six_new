package com.dream.six.vo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PaymentRequestDTO {
    private String paymentMethod; // BANK or UPI

    // Bank fields
    private String accountName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;

    // UPI fields
    private String upiId;
    private String upiPhone;
    private MultipartFile qrCode;

    private Double amount;
}
