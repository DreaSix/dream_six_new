package com.dream.six.repository;

import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.WinnerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface WinnerDetailsRepository extends JpaRepository<WinnerDetails, UUID> {
    List<WinnerDetails> findByCreatedAtBetween(Timestamp startOfYesterday, Timestamp endOfToday);
}
