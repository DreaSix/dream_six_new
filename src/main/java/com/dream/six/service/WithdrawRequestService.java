package com.dream.six.service;

import com.dream.six.vo.request.WithdrawBankRequestDTO;
import com.dream.six.vo.response.WithdrawBankResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WithdrawRequestService {

    WithdrawBankResponseDTO createWithdrawRequest(WithdrawBankRequestDTO requestDTO);

    WithdrawBankResponseDTO getWithdrawRequestById(UUID id);

    List<WithdrawBankResponseDTO> getWithdrawBanksBYUser();

    List<WithdrawBankResponseDTO> getAllWithdrawRequests();

    void deleteWithdrawRequest(UUID id);
}
