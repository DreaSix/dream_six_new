package com.dream.six.repository;

import com.dream.six.entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, UUID> {

    Optional<UserAuthEntity> findByUserNameAndIsDeletedFalse(String username);

    boolean existsByUserNameAndIsDeletedFalse(String email);

    Optional<UserAuthEntity> findByUserIdAndIsDeletedFalse(UUID userUUID);
}
