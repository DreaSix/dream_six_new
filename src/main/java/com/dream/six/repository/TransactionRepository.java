package com.dream.six.repository;

import com.dream.six.entity.Transaction;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByTransactionType(TransactionType transactionType);

    Page<Transaction> findAllByCreatedByUUID(PageRequest pageable, UUID userId);

    List<Transaction> findByApprovedBy(UserInfoEntity userInfoEntity);
}
