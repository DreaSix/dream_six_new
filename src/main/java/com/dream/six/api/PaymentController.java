package com.dream.six.api;

import com.dream.six.service.PaymentService;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.PaymentRequestDTO;
import com.dream.six.vo.response.PaymentResponseDTO;
import com.dream.six.constants.ApiResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> createPayment(@ModelAttribute PaymentRequestDTO requestDTO) throws Exception {
        log.info("Received request to create payment: {}", requestDTO);
        PaymentResponseDTO response = paymentService.createPayment(requestDTO);
        log.info("Payment created successfully with ID: {}", response.getId());
        ApiResponse<PaymentResponseDTO> apiResponse = ApiResponse.<PaymentResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.PAYMENT_CREATED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPaymentById(@PathVariable UUID id) {
        log.info("Received request to fetch payment with ID: {}", id);
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        log.info("Fetched payment successfully with ID: {}", id);
        ApiResponse<PaymentResponseDTO> apiResponse = ApiResponse.<PaymentResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.PAYMENT_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> updatePayment(@PathVariable UUID id, @RequestBody PaymentRequestDTO requestDTO) throws IOException {
        log.info("Received request to update payment with ID: {}", id);
        PaymentResponseDTO response = paymentService.updatePayment(id, requestDTO);
        log.info("Payment updated successfully with ID: {}", id);
        ApiResponse<PaymentResponseDTO> apiResponse = ApiResponse.<PaymentResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.PAYMENT_UPDATED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable UUID id) {
        log.info("Received request to delete payment with ID: {}", id);
        paymentService.deletePayment(id);
        log.info("Payment deleted successfully with ID: {}", id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ApiResponseMessages.PAYMENT_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponseDTO>>> getAllPayments() {
        log.info("Received request to fetch all payments");
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        log.info("Fetched all payments successfully");
        ApiResponse<List<PaymentResponseDTO>> apiResponse = ApiResponse.<List<PaymentResponseDTO>>builder()
                .data(payments)
                .message(ApiResponseMessages.PAYMENTS_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
