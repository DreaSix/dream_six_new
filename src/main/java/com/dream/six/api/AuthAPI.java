package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.service.LoginService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.LoginRequestVO;
import com.dream.six.vo.request.ValidateTokenRequestVO;
import com.dream.six.vo.response.JwtResponseVO;
import com.dream.six.vo.response.ValidateTokenResponseVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthAPI {

    private final LoginService loginService;

    public AuthAPI(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<JwtResponseVO>> authenticateUser(@Valid @RequestBody LoginRequestVO loginRequest) throws ExecutionException, InterruptedException {
        log.info("Received login request for user: {}", loginRequest.getUserName());
        JwtResponseVO jwtResponseVO = loginService.authenticateUser(loginRequest);
        log.info("User authenticated successfully: {}", jwtResponseVO.getUsername());
        ApiResponse<JwtResponseVO> response = ApiResponse.<JwtResponseVO>builder()
                .data(jwtResponseVO)
                .message(ApiResponseMessages.USER_LOGGED_IN_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ValidateTokenResponseVO>> validateToken(@RequestBody ValidateTokenRequestVO request) {
        log.info("Received token validation request: {}", request);
        ValidateTokenResponseVO responseDTO = loginService.validateToken(request);
        log.info("Token validation successful for token: {}", request.getToken());
        ApiResponse<ValidateTokenResponseVO> response = ApiResponse.<ValidateTokenResponseVO>builder()
                .data(responseDTO)
                .message(ApiResponseMessages.TOKEN_VALIDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

}
