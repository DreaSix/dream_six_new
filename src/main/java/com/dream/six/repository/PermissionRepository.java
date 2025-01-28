package com.dream.six.repository;

import com.dream.six.entity.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {

    Optional<PermissionEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT c FROM PermissionEntity c WHERE c.isDeleted = false AND (:featureName IS NULL OR c.featureName = :featureName) ORDER BY c.createdAt DESC")
    Page<PermissionEntity> findAllByFeatureNameOrDeletedFalse(@Param("featureName") String featureName, Pageable pageable);

    @Query("SELECT c FROM PermissionEntity c WHERE c.id IN :permissionIds AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<PermissionEntity> findAllByIdInDeletedFalse(@Param("permissionIds") List<UUID> permissionIds);

    Boolean existsByFeatureNameAndIsDeletedFalse(String permissionName);

    Optional<PermissionEntity> findByFeatureNameAndEndPointNameAndTypeAndRoutePathAndPermissionNameAndKeyAndIsDeletedFalse(
            String featureName, String endPointName, String type, String routePath, String permissionName, String key);
}
