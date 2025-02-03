package com.dream.six.repository;

import com.dream.six.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<BidEntity, UUID> {
    BidEntity findByMatchIdAndPlayerId(UUID matchId, UUID playerId);
}

