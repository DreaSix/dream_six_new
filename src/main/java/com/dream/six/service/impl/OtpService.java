package com.dream.six.service.impl;

import com.dream.six.config.Msg91Config;
import com.dream.six.vo.OtpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final RestTemplate restTemplate;
    private final Msg91Config msg91Config;

    private static final String SEND_OTP_URL = "https://api.msg91.com/api/v5/widget/sendOtp";
    private static final String VERIFY_OTP_URL = "https://api.msg91.com/api/v5/widget/verifyOtp";

    /**
     * Sends OTP to the given mobile number.
     * Extracts the `reqId` from the response and returns it.
     */
    public OtpResponse sendOtp(String mobileNumber) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("identifier", "91" + mobileNumber);
        requestBody.put("requestVariables", Map.of("captchaRenderId", "root"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OtpResponse> response = restTemplate.exchange(
                    SEND_OTP_URL, HttpMethod.POST, requestEntity, OtpResponse.class);

            OtpResponse otpResponse = response.getBody();

            // ✅ Extract reqId from message if available
            if (otpResponse != null && otpResponse.getMessage() != null) {
                otpResponse.setReqId(otpResponse.getMessage());
            }

            log.info("OTP Sent Successfully: {}", otpResponse);
            return otpResponse;
        } catch (Exception ex) {
            log.error("Error sending OTP: {}", ex.getMessage());
            return new OtpResponse(false, "Error sending OTP","");
        }
    }

    /**
     * Verifies the OTP using the reqId from sendOtp.
     */
    public OtpResponse verifyOtp(String otp, String mobileNumber, String reqId) {
        if (reqId == null || reqId.isEmpty()) {
            throw new IllegalArgumentException("reqId is required for OTP verification.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("otp", otp);
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("identifier", "91" + mobileNumber);
        requestBody.put("reqId", reqId); // ✅ Send reqId

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OtpResponse> response = restTemplate.exchange(
                    VERIFY_OTP_URL, HttpMethod.POST, requestEntity, OtpResponse.class);

            log.info("OTP Verified Successfully: {}", response.getBody());
            return response.getBody();
        } catch (Exception ex) {
            log.error("Error verifying OTP: {}", ex.getMessage());
            return new OtpResponse(false, "Error verifying OTP","");
        }
    }
}
