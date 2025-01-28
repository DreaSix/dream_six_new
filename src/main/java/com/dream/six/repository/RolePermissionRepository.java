package com.dream.six.repository;

import com.dream.six.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, UUID> {

    @Query("SELECT c FROM RolePermissionEntity c WHERE c.role.id IN :roleIds AND c.isDeleted = false AND c.revoked = false ORDER BY c.createdAt DESC")
    List<RolePermissionEntity> findAllByRoleIdsInDeletedFalse(@Param("roleIds") List<UUID> roleIds);

    @Query("SELECT c FROM RolePermissionEntity c WHERE c.role.id = :roleId AND c.isDeleted = false AND c.revoked = false ORDER BY c.createdAt DESC")
    List<RolePermissionEntity> findAllByRoleIdsInDeletedFalse(@Param("roleId") UUID roleId);

    @Query("SELECT c FROM RolePermissionEntity c WHERE c.permission.id = :permissionId AND c.isDeleted = false AND c.revoked = false ORDER BY c.createdAt DESC")
    List<RolePermissionEntity> findAllByPermissionIdsInDeletedFalse(@Param("permissionId") UUID permissionId);

  @Query(
      "SELECT c FROM RolePermissionEntity c WHERE c.role.id IN :roleIds AND c.revoked = false AND c.revoked = false ORDER BY c.createdAt DESC")
  List<RolePermissionEntity> findAllByRoleIdsIsRevokedFalse(@Param("roleIds") List<UUID> roleIds);
}
