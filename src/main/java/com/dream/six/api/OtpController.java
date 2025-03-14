package com.dream.six.api;


import com.dream.six.service.impl.OtpService;
import com.dream.six.vo.OtpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String mobileNumber) {
        String response = otpService.sendOtp(mobileNumber);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp( @RequestParam String otp,@RequestParam String reqId) {
        String response = otpService.verifyOtp(otp,reqId);
        return ResponseEntity.ok(response);
    }
}
