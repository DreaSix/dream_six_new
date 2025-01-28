package com.dream.six.vo.request;

import com.dream.six.enums.TransactionType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class TransactionRequestDTO {
    private Double amount;
    private MultipartFile transactionImage;
    private String utr;
    private UUID paymentId;
    private TransactionType transactionType;

}
