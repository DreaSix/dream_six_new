package com.dream.six.repository;

import com.dream.six.entity.WithdrawBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawBankEntity, UUID> {
    List<WithdrawBankEntity> findByCreatedByUUID(UUID uuid);
}
