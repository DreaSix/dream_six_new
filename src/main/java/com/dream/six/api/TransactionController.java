package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.Status;
import com.dream.six.enums.TransactionType;
import com.dream.six.service.TransactionService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.CreateWithdrawRequestDTO;
import com.dream.six.vo.request.TransactionRequestDTO;
import com.dream.six.vo.request.UpdateTransactionDTO;
import com.dream.six.vo.response.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(@ModelAttribute TransactionRequestDTO requestDTO) throws IOException {
        log.info("Received request to create transaction: {}", requestDTO);
        TransactionResponseDTO response = transactionService.createTransaction(requestDTO);
        log.info("Transaction created successfully with ID: {}", response.getId());

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.TRANSACTION_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> getTransactionById(@PathVariable UUID id) {
        log.info("Received request to fetch transaction with ID: {}", id);
        TransactionResponseDTO response = transactionService.getTransactionById(id);
        log.info("Transaction fetched successfully with ID: {}", id);

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.TRANSACTION_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getAllTransactions() {
        log.info("Received request to fetch all transactions");
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
        log.info("All transactions fetched successfully");

        ApiResponse<List<TransactionResponseDTO>> apiResponse = ApiResponse.<List<TransactionResponseDTO>>builder()
                .data(transactions)
                .message(ApiResponseMessages.ALL_TRANSACTIONS_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-transactions-by-user")
    public ResponseEntity<ApiPageResponse<List<TransactionResponseDTO>>> getAllTransactionsByUserId(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int pageSize
    ) {
        // Get authenticated user information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity userInfoEntity = (UserInfoEntity) authentication.getPrincipal();

        // Log the operation
        log.info("Fetching transactions for user {} with page {} and size {}.", userInfoEntity.getId(), pageNumber, pageSize);

        // Fetch transactions for the user
        var transactions = transactionService.getAllTransactionByUserId(pageNumber, pageSize, userInfoEntity.getId());

        // Add a success message to the response
        transactions.setMessage(ApiResponseMessages.ALL_TRANSACTIONS_FETCHED_SUCCESSFULLY);

        // Return the response
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(
            @PathVariable UUID id,
            @ModelAttribute TransactionRequestDTO requestDTO) throws IOException {
        log.info("Received request to update transaction with ID: {}", id);
        TransactionResponseDTO response = transactionService.updateTransaction(id, requestDTO);
        log.info("Transaction updated successfully with ID: {}", id);

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.TRANSACTION_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable UUID id) {
        log.info("Received request to delete transaction with ID: {}", id);
        transactionService.deleteTransaction(id);
        log.info("Transaction deleted successfully with ID: {}", id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message(ApiResponseMessages.TRANSACTION_DELETED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/approval-status")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateApprovalStatus(
            @PathVariable UUID id,
            @RequestParam String approvalStatus) { // Accept as String
        log.info("Updating approval status for transaction ID: {} to {}", id, approvalStatus);

        // Convert String to Enum manually
        Status statusEnum;
        try {
            statusEnum = Status.valueOf(approvalStatus.toUpperCase()); // Convert to uppercase
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid approval status: " + approvalStatus);
        }

        TransactionResponseDTO response = transactionService.updateApprovalStatus(id, statusEnum);
        log.info("Approval status updated successfully for transaction ID: {}", id);

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .data(response)
                .message(ApiResponseMessages.APPROVAL_STATUS_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getTransactionsByTypeAndStatus(
            @RequestParam TransactionType transactionType) {
        log.info("Fetching transactions with type: {}", transactionType);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByType(transactionType);
        log.info("Transactions fetched successfully with type: {}", transactionType);

        ApiResponse<List<TransactionResponseDTO>> apiResponse = ApiResponse.<List<TransactionResponseDTO>>builder()
                .data(transactions)
                .message(ApiResponseMessages.TRANSACTIONS_FILTERED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/withdraw/request")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> createWithdrawRequest(
            @RequestBody CreateWithdrawRequestDTO requestDTO) {
        log.info("Creating withdraw request for amount: {}", requestDTO.getAmount());

         transactionService.createWithdrawRequest(requestDTO);
        log.info("Withdraw request created successfully with UTR");

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .message(ApiResponseMessages.WITHDRAW_REQUEST_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/withdraw/approve")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(
            @ModelAttribute UpdateTransactionDTO updateTransactionDTO) throws IOException {
        log.info("Updating transaction with ID: {}", updateTransactionDTO.getTransactionId());

        TransactionResponseDTO updatedTransaction = transactionService.updateWithdrawTransaction(updateTransactionDTO);
        log.info("Transaction updated successfully with ID: {}", updatedTransaction.getId());

        ApiResponse<TransactionResponseDTO> apiResponse = ApiResponse.<TransactionResponseDTO>builder()
                .data(updatedTransaction)
                .message(ApiResponseMessages.TRANSACTION_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


}
