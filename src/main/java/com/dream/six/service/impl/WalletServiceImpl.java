package com.dream.six.service.impl;

import com.dream.six.entity.WalletEntity;
import com.dream.six.repository.WalletRepository;
import com.dream.six.service.WalletService;
import com.dream.six.vo.response.WalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletResponse getWalletDetailsByUser(UUID userUUID) {
        WalletEntity wallet = walletRepository.findByCreatedByUUID(userUUID)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + userUUID));

        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getNetExposure());
    }

    @Override
    @Transactional
    public WalletResponse depositAmount(UUID userUUID, BigDecimal amount) {
        log.info("Processing wallet deposit. User: {}, Amount: {}", userUUID, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        WalletEntity wallet = walletRepository.findByCreatedByUUID(userUUID)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user UUID: " + userUUID));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        log.info("Deposit successful. New balance: {}", wallet.getBalance());

        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getNetExposure());
    }

    @Override
    public List<WalletResponse> getAllWallets() {
        log.info("Fetching all wallet details.");
        List<WalletEntity> wallets = walletRepository.findAll();

        return wallets.stream()
                .map(wallet -> new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getNetExposure()))
                .collect(Collectors.toList());
    }
}
