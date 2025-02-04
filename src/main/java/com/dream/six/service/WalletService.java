package com.dream.six.service;

import com.dream.six.vo.response.WalletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletResponse getWalletDetailsByUser(UUID userUUID);
    WalletResponse depositAmount(UUID userUUID, BigDecimal amount);
    List<WalletResponse> getAllWallets();
}
