package com.dream.six.repository;

import com.dream.six.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {

    @Query("SELECT t FROM TokenEntity t WHERE t.userInfo.id = :userId AND (t.expired = :expired OR t.revoked = :revoked)")
    List<TokenEntity> findAllByUserInfoIdAndExpiredIsOrRevokedIs(UUID userId, boolean expired, boolean revoked);

    Optional<TokenEntity> findByJwtToken(String authToken);

    List<TokenEntity> findAllByJwtTokenAndExpiredIsOrRevokedIs(String jwtToken, boolean b, boolean b1);

    @Query(value = """
            select t from TokenEntity t inner join UserInfoEntity u\s
            on t.userInfo.id = u.id\s
            where u.id = :id and (t.expired = false or t.revoked = false)\s
            """)
    List<TokenEntity> findAllValidTokenByUser(UUID id);
}
