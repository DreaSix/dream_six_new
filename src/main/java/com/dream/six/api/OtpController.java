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
    public ResponseEntity<OtpResponse> sendOtp(@RequestParam String mobileNumber) {
        OtpResponse response = otpService.sendOtp(mobileNumber);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/verify")
    public ResponseEntity<OtpResponse> verifyOtp(@RequestParam String mobileNumber, @RequestParam String otp) {
        OtpResponse response = otpService.verifyOtp(otp, mobileNumber);
        return ResponseEntity.ok(response);
    }
}
