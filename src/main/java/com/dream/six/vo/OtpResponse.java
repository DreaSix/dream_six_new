package com.dream.six.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Data;

@Data
@AllArgsConstructor
public class OtpResponse {
    private boolean success;
    private String message;
    private String reqId; // ✅ Store reqId separately
}
