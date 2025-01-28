package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.service.WithdrawRequestService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.WithdrawBankRequestDTO;
import com.dream.six.vo.response.WithdrawBankResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/withdraw")
@Slf4j
@RequiredArgsConstructor
public class WithdrawRequestController {

    private final WithdrawRequestService withdrawRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<WithdrawBankResponseDTO>> createWithdrawRequest(@RequestBody WithdrawBankRequestDTO requestDTO) {
        log.info("Received request to create withdraw request: {}", requestDTO);
        WithdrawBankResponseDTO responseDTO = withdrawRequestService.createWithdrawRequest(requestDTO);
        log.info("Withdraw request created successfully with ID: {}", responseDTO.getId());

        ApiResponse<WithdrawBankResponseDTO> apiResponse = ApiResponse.<WithdrawBankResponseDTO>builder()
                .data(responseDTO)
                .message(ApiResponseMessages.WITHDRAW_REQUEST_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WithdrawBankResponseDTO>> getWithdrawRequestById(@PathVariable UUID id) {
        log.info("Received request to fetch withdraw request with ID: {}", id);
        WithdrawBankResponseDTO responseDTO = withdrawRequestService.getWithdrawRequestById(id);
        log.info("Withdraw request fetched successfully with ID: {}", id);

        ApiResponse<WithdrawBankResponseDTO> apiResponse = ApiResponse.<WithdrawBankResponseDTO>builder()
                .data(responseDTO)
                .message(ApiResponseMessages.WITHDRAW_REQUEST_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WithdrawBankResponseDTO>>> getAllWithdrawRequests() {
        log.info("Received request to fetch all withdraw requests");
        List<WithdrawBankResponseDTO> responseDTOs = withdrawRequestService.getAllWithdrawRequests();
        log.info("All withdraw requests fetched successfully");

        ApiResponse<List<WithdrawBankResponseDTO>> apiResponse = ApiResponse.<List<WithdrawBankResponseDTO>>builder()
                .data(responseDTOs)
                .message(ApiResponseMessages.ALL_WITHDRAW_REQUESTS_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWithdrawRequest(@PathVariable UUID id) {
        log.info("Received request to delete withdraw request with ID: {}", id);
        withdrawRequestService.deleteWithdrawRequest(id);
        log.info("Withdraw request deleted successfully with ID: {}", id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ApiResponseMessages.WITHDRAW_REQUEST_DELETED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
