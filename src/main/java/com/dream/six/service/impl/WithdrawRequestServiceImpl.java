package com.dream.six.service.impl;

import com.dream.six.constants.Constants;
import com.dream.six.entity.WithdrawBankEntity;
import com.dream.six.repository.WithdrawRequestRepository;
import com.dream.six.service.WithdrawRequestService;
import com.dream.six.vo.request.WithdrawBankRequestDTO;
import com.dream.six.vo.response.WithdrawBankResponseDTO;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WithdrawRequestServiceImpl implements WithdrawRequestService {

    private final WithdrawRequestRepository withdrawRequestRepository;

    public WithdrawRequestServiceImpl(WithdrawRequestRepository withdrawRequestRepository) {
        this.withdrawRequestRepository = withdrawRequestRepository;
    }

    @Override
    public WithdrawBankResponseDTO createWithdrawRequest(WithdrawBankRequestDTO requestDTO) {
        WithdrawBankEntity entity = new WithdrawBankEntity();
        entity.setBankName(requestDTO.getBankName());
        entity.setAccountHolderName(requestDTO.getAccountHolderName());
        entity.setAccountNumber(requestDTO.getAccountNumber());
        entity.setIfscCode(requestDTO.getIfscCode());
        entity.setCreatedBy(String.valueOf(MDC.get(Constants.USERNAME_ATTRIBUTE)));
        String userUUIDString = org.slf4j.MDC.get(Constants.USER_UUID_ATTRIBUTE);
        entity.setCreatedByUUID(UUID.fromString(userUUIDString));
        WithdrawBankEntity savedEntity = withdrawRequestRepository.save(entity);

        return mapToResponseDTO(savedEntity);
    }

    @Override
    public WithdrawBankResponseDTO getWithdrawRequestById(UUID id) {
        WithdrawBankEntity entity = withdrawRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdraw Request not found with ID: " + id));
        return mapToResponseDTO(entity);
    }

    @Override
    public List<WithdrawBankResponseDTO> getWithdrawBanksBYUser() {
        String userUUIDString = org.slf4j.MDC.get(Constants.USER_UUID_ATTRIBUTE);

        return withdrawRequestRepository.findByCreatedByUUID(UUID.fromString(userUUIDString))
                .stream()
                .map(this::mapToResponseDTO)
                .toList();    }

    @Override
    public List<WithdrawBankResponseDTO> getAllWithdrawRequests() {
        return withdrawRequestRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public void deleteWithdrawRequest(UUID id) {
        withdrawRequestRepository.deleteById(id);
    }

    private WithdrawBankResponseDTO mapToResponseDTO(WithdrawBankEntity entity) {
        WithdrawBankResponseDTO responseDTO = new WithdrawBankResponseDTO();
        responseDTO.setId(entity.getId());
        responseDTO.setBankName(entity.getBankName());
        responseDTO.setAccountNumber(entity.getAccountNumber());
        responseDTO.setAccountHolderName(entity.getAccountHolderName());
        responseDTO.setIfscCode(entity.getIfscCode());
        return responseDTO;
    }
}
