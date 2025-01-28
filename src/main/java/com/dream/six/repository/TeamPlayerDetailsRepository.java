package com.dream.six.repository;


import com.dream.six.entity.MatchDetails;
import com.dream.six.entity.TeamPlayerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamPlayerDetailsRepository extends JpaRepository<TeamPlayerDetails, UUID> {
    List<TeamPlayerDetails> findByMatchDetails(MatchDetails matchDetails);

    List<TeamPlayerDetails> findByMatchDetailsAndTeamName(MatchDetails matchDetails, String teamName);
}
