package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.constants.Constants;
import com.dream.six.service.WalletService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.response.WalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@Slf4j
@RequiredArgsConstructor
public class WalletAPI {

    private final WalletService walletService;

    @GetMapping("/get-wallet-details-by-user")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletDetailsByUser() {
        log.info("Received request to fetch wallet details.");
        UUID userUUID = UUID.fromString(MDC.get(Constants.USER_UUID_ATTRIBUTE));

        WalletResponse walletResponse = walletService.getWalletDetailsByUser(userUUID);

        ApiResponse<WalletResponse> apiResponse = ApiResponse.<WalletResponse>builder()
                .data(walletResponse)
                .message(ApiResponseMessages.WALLET_DETAILS_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getAllWallets() {
        log.info("Received request to fetch all wallets.");

        List<WalletResponse> wallets = walletService.getAllWallets();

        ApiResponse<List<WalletResponse>> apiResponse = ApiResponse.<List<WalletResponse>>builder()
                .data(wallets)
                .message(ApiResponseMessages.ALL_WALLET_DETAILS_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<WalletResponse>> depositAmount(@RequestParam double amount) {
        UUID userUUID = UUID.fromString(MDC.get(Constants.USER_UUID_ATTRIBUTE));

        log.info("Received deposit request for user {} with amount {}", userUUID, amount);

        WalletResponse walletResponse = walletService.depositAmount(userUUID, BigDecimal.valueOf(amount));

        ApiResponse<WalletResponse> apiResponse = ApiResponse.<WalletResponse>builder()
                .data(walletResponse)
                .message(ApiResponseMessages.AMOUNT_ADD_WALLET_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
