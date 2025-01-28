package com.dream.six.repository;

import com.dream.six.entity.MatchDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MatchDetailsRepository extends JpaRepository<MatchDetails, UUID> {

}
