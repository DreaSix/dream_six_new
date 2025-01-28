package com.dream.six.repository;

import com.dream.six.entity.PlayerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerDetailsRepository extends JpaRepository<PlayerDetails, UUID> {

    @Query("SELECT p FROM PlayerDetails p WHERE p.id IN :playerIds")
    List<PlayerDetails> findAllByPlayerIds(@Param("playerIds") List<UUID> playerIds);

}
