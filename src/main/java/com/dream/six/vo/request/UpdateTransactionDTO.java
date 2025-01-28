package com.dream.six.vo.request;

import com.dream.six.enums.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class UpdateTransactionDTO {
    private UUID transactionId;
    private Status approvalStatus;
    private MultipartFile transactionImage;
}
