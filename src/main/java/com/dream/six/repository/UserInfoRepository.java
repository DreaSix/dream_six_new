package com.dream.six.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.dream.six.entity.UserInfoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, UUID>{

    Optional<UserInfoEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT c FROM UserInfoEntity c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<UserInfoEntity> findAllByIsDeletedFalse(Pageable pageable);

    Optional<UserInfoEntity> findByPhoneNumberAndIsDeletedFalse(String email);

    boolean existsByPhoneNumberAndIsDeletedFalse(String email);

    @Query("SELECT c FROM UserInfoEntity c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    List<UserInfoEntity> findAllByIsDeletedFalse();

    List<UserInfoEntity> findByIdIn(List<UUID> ids);

    List<UserInfoEntity> findByRoles_NameOrderByCreatedAtDesc(String roleName);


}
