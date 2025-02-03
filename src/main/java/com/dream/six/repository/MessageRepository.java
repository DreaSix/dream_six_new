package com.dream.six.repository;

import com.dream.six.entity.MessageDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageDetails, UUID> {
    List<MessageDetails> findByBidId(UUID bidId);

    void deleteByBidId(UUID id);
}
