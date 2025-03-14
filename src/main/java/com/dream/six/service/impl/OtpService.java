package com.dream.six.service.impl;

import com.dream.six.config.Msg91Config;
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


    public String sendOtp(String mobileNumber) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("identifier", "91" + mobileNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Cookie", "HELLO_APP_HASH=c2t3U1dXQ1lWeTNvRXJ4T1BTWUFINkc0L0pGekhFcFNwd2lzTG9xdVk5OD0%3D; PHPSESSID=v23t6lc1124j41jnhkdbjoin56");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(SEND_OTP_URL, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

    public String verifyOtp(String otp, String reqId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("tokenAuth", msg91Config.getAuthKey());
        requestBody.put("widgetId", msg91Config.getWidgetId());
        requestBody.put("otp", otp);
        requestBody.put("reqId", reqId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
       // headers.set("Cookie", "HELLO_APP_HASH=c2t3U1dXQ1lWeTNvRXJ4T1BTWUFINkc0L0pGekhFcFNwd2lzTG9xdVk5OD0%3D; PHPSESSID=v23t6lc1124j41jnhkdbjoin56");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(VERIFY_OTP_URL, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

}
