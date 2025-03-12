package com.dream.six.service.impl;


import com.amazonaws.services.glue.model.EntityNotFoundException;
import com.dream.six.constants.Constants;
import com.dream.six.entity.*;
import com.dream.six.enums.Status;
import com.dream.six.enums.TransactionType;
import com.dream.six.repository.*;
import com.dream.six.service.TransactionService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.CreateWithdrawRequestDTO;
import com.dream.six.vo.request.TransactionRequestDTO;
import com.dream.six.vo.request.UpdateTransactionDTO;
import com.dream.six.vo.response.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dream.six.mapper.CommonMapper.mapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final WalletRepository walletRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) throws IOException {
        log.info("Creating transaction: {}", requestDTO);

        Payment payment = paymentRepository.findById(requestDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found for ID: " + requestDTO.getPaymentId()));

        Transaction transaction = new Transaction();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setImage(requestDTO.getTransactionImage().getBytes());
        transaction.setUtr(requestDTO.getUtr());
        transaction.setPayment(payment);
        transaction.setTransactionType(requestDTO.getTransactionType());
        transaction.setApprovalStatus(Status.PENDING);
        String userUUIDString = MDC.get(Constants.USER_UUID_ATTRIBUTE);
        Optional<UserInfoEntity> userInfo = userInfoRepository.findByIdAndIsDeletedFalse(UUID.fromString(userUUIDString));
        if (userInfo.isPresent()) {
            transaction.setCreatedByUUID(userInfo.get().getId());
        } else {
            log.error("User UUID is missing in MDC");
            throw new IllegalArgumentException("User UUID is missing in MDC");
        }
        transaction.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));

        Transaction savedTransaction = transactionRepository.save(transaction);


        return mapper.convertEntityToTransactionResponseDTO(savedTransaction);
    }

    @Override
    public TransactionResponseDTO getTransactionById(UUID id) {
        log.info("Fetching transaction with ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found for ID: " + id));

        return mapper.convertEntityToTransactionResponseDTO(transaction);
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        log.info("Fetching all transactions");

        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .map(item -> {
                    TransactionResponseDTO transactionResponseDTO = mapper.convertEntityToTransactionResponseDTO(item);
                    transactionResponseDTO.setUserName(item.getCreatedBy());
                    if (item.getWithdrawBank() != null){
                        transactionResponseDTO.setAccountHolderName(item.getWithdrawBank().getAccountHolderName());
                        transactionResponseDTO.setAccountNumber(item.getWithdrawBank().getAccountNumber());
                        transactionResponseDTO.setIfscCode(item.getWithdrawBank().getIfscCode());
                    }
                    if (item.getImage() != null){
                        byte[] imageBytes = item.getImage();

                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                        transactionResponseDTO.setTransactionImage(base64Image);
                    }

                    return transactionResponseDTO;
                })
                .toList();
    }

    @Override
    public ApiPageResponse<List<TransactionResponseDTO>> getAllTransactionByUserId(int pageNumber, int pageSize, UUID userId) {
        // Create a pageable object
        var pageable = PageRequest.of(pageNumber, pageSize);

        // Fetch paginated results from the repository
        var transactionPage = transactionRepository.findAllByCreatedByUUID(pageable, userId);

        // Convert entities to DTOs
        var responseVOs = transactionPage.getContent()
                .stream()
                .map(mapper::convertEntityToTransactionResponseDTO)
                .toList();

        log.info("Converted {} transactions to response DTOs", responseVOs.size());

        // Build and return the API response
        return ApiPageResponse.<List<TransactionResponseDTO>>builder()
                .totalContent(responseVOs) // Current page's content
                .totalCount(transactionPage.getTotalElements()) // Total number of items across all pages
                .build();
    }

    @Override
    public TransactionResponseDTO updateTransaction(UUID id, TransactionRequestDTO requestDTO) throws IOException {
        log.info("Updating transaction with ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found for ID: " + id));

        Payment payment = paymentRepository.findById(requestDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found for ID: " + requestDTO.getPaymentId()));

        transaction.setAmount(requestDTO.getAmount());
        transaction.setImage(requestDTO.getTransactionImage().getBytes());
        transaction.setUtr(requestDTO.getUtr());
        transaction.setPayment(payment);
        transaction.setTransactionType(requestDTO.getTransactionType());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapper.convertEntityToTransactionResponseDTO(updatedTransaction);
    }

    @Override
    public void deleteTransaction(UUID id) {
        log.info("Deleting transaction with ID: {}", id);

        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found for ID: " + id);
        }

        transactionRepository.deleteById(id);
    }

    @Override
    public TransactionResponseDTO updateApprovalStatus(UUID id, Status approvalStatus) {
        log.info("Updating approval status for transaction ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found for ID: " + id));

        transaction.setApprovalStatus(approvalStatus);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        WalletEntity walletEntity = walletRepository.findByCreatedByUUID(transaction.getCreatedByUUID())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + transaction.getCreatedByUUID()));
        BigDecimal newBalance = walletEntity.getBalance().add(BigDecimal.valueOf(transaction.getAmount()));
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        walletEntity.setBalance(newBalance);
        walletRepository.save(walletEntity);
        return mapper.convertEntityToTransactionResponseDTO(updatedTransaction);
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByType(TransactionType transactionType) {
        log.info("Fetching transactions by type: {} ", transactionType);

        List<Transaction> transactions = transactionRepository.findByTransactionType(transactionType);

        return transactions.stream()
                .map(mapper::convertEntityToTransactionResponseDTO)
                .toList();
    }

    @Override
    public void createWithdrawRequest(CreateWithdrawRequestDTO requestDTO) {
        WithdrawBankEntity withdrawBank = withdrawRequestRepository.findById(requestDTO.getWithdrawBankId())
                .orElseThrow(() -> new EntityNotFoundException("WithdrawBankEntity not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setApprovalStatus(Status.PENDING); // Default status
        transaction.setWithdrawBank(withdrawBank);
        String userUUIDString = MDC.get(Constants.USER_UUID_ATTRIBUTE);
        if (userUUIDString != null && !userUUIDString.isEmpty()) {
            transaction.setCreatedByUUID(UUID.fromString(userUUIDString));
        } else {
            log.error("User UUID is missing in MDC");
            throw new IllegalArgumentException("User UUID is missing in MDC");
        }
        transaction.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));

        transactionRepository.save(transaction);

        UUID userUUID;
        try {
            userUUID = UUID.fromString(MDC.get(Constants.USER_UUID_ATTRIBUTE));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid UUID format in MDC: " + MDC.get(Constants.USER_UUID_ATTRIBUTE), e);
        }

        WalletEntity walletEntity = walletRepository.findByCreatedByUUID(userUUID)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + userUUID));
        BigDecimal newBalance = walletEntity.getBalance().subtract(BigDecimal.valueOf(transaction.getAmount()));
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        walletEntity.setBalance(newBalance);
        walletRepository.save(walletEntity);

    }

    @Override
    public TransactionResponseDTO updateWithdrawTransaction(UpdateTransactionDTO updateTransactionDTO) throws IOException {
        Transaction transaction = transactionRepository.findById(updateTransactionDTO.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        transaction.setApprovalStatus(updateTransactionDTO.getApprovalStatus());

        UUID userUUID;
        try {
            userUUID = UUID.fromString(MDC.get(Constants.USER_UUID_ATTRIBUTE));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new RuntimeException("Invalid UUID format in MDC: " + MDC.get(Constants.USER_UUID_ATTRIBUTE), e);
        }

        WalletEntity walletEntity = walletRepository.findByCreatedByUUID(transaction.getCreatedByUUID())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + transaction.getCreatedByUUID()));

        if (!"APPROVED".equalsIgnoreCase(String.valueOf(updateTransactionDTO.getApprovalStatus()))) {
            BigDecimal newBalance = walletEntity.getBalance().add(BigDecimal.valueOf(transaction.getAmount()));
            walletEntity.setBalance(newBalance);
        }

//        WalletEntity walletEntity = walletRepository.findByCreatedByUUID(userUUID)
//                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + userUUID));
//        BigDecimal newBalance = walletEntity.getBalance().subtract(BigDecimal.valueOf(transaction.getAmount()));
//        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
//            throw new RuntimeException("Insufficient balance");
//        }
//        walletEntity.setBalance(newBalance);
        walletRepository.save(walletEntity);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapper.convertEntityToTransactionResponseDTO(updatedTransaction);
    }
}
