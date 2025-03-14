package com.dream.six.service.impl;

import com.dream.six.config.Msg91Config;
import com.dream.six.vo.OtpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RestTemplate restTemplate;
    private final Msg91Config msg91Config;

    private static final String SEND_OTP_URL = "https://api.msg91.com/api/v5/widget/sendOtp";
    private static final String VERIFY_OTP_URL = "https://api.msg91.com/api/v5/widget/verifyOtp";

    public OtpResponse sendOtp(String mobileNumber) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("identifier", "91" + mobileNumber);
        requestBody.put("requestVariables", Map.of("captchaRenderId", "root"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<OtpResponse> response = restTemplate.exchange(SEND_OTP_URL, HttpMethod.POST, requestEntity, OtpResponse.class);

        return response.getBody();
    }

    public OtpResponse verifyOtp(String otp, String mobileNumber, String reqId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("otp", otp);
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("identifier", "91" + mobileNumber);
        requestBody.put("reqId", reqId); // ✅ Added reqId

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<OtpResponse> response = restTemplate.exchange(VERIFY_OTP_URL, HttpMethod.POST, requestEntity, OtpResponse.class);

        return response.getBody();
    }


}
