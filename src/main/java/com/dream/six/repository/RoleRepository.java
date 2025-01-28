package com.dream.six.repository;

import com.dream.six.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    @Query("SELECT c FROM RoleEntity c WHERE c.id IN :roleIds AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<RoleEntity> findAllByIdInDeletedFalse(List<UUID> roleIds);

    Optional<RoleEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT c FROM RoleEntity c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<RoleEntity> findAllByIsDeletedFalse(Pageable pageable);

    Optional<RoleEntity> findByNameAndIsDeletedFalse(String roleEnum);

    boolean existsByNameAndIsDeletedFalse(String roleEnum);

    @Query("SELECT c FROM RoleEntity c WHERE c.name IN :roleNames AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<RoleEntity> findByListOfNamesAndIsDeletedFalse(List<String> roleNames);


}
