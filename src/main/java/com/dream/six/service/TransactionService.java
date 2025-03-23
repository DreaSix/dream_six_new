package com.dream.six.service;


import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.Status;
import com.dream.six.enums.TransactionType;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.CreateWithdrawRequestDTO;
import com.dream.six.vo.request.TransactionRequestDTO;
import com.dream.six.vo.request.UpdateTransactionDTO;
import com.dream.six.vo.response.TransactionResponseDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO) throws IOException;

    TransactionResponseDTO getTransactionById(UUID id);

    List<TransactionResponseDTO> getAllTransactions();

    ApiPageResponse<List<TransactionResponseDTO>> getAllTransactionByUserId(int pageNumber, int pageSize, UUID userId);

    TransactionResponseDTO updateTransaction(UUID id, TransactionRequestDTO requestDTO) throws IOException;

    void deleteTransaction(UUID id);

    TransactionResponseDTO updateApprovalStatus(UUID id, Status approvalStatus);

    List<TransactionResponseDTO> getTransactionsByType(TransactionType transactionType);

    void createWithdrawRequest(CreateWithdrawRequestDTO requestDTO);
    TransactionResponseDTO updateWithdrawTransaction(UpdateTransactionDTO updateTransactionDTO, UserInfoEntity userInfoEntity) throws IOException;


    }
