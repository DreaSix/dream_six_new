package com.dream.six.service;


import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.AssignRoleRequestVO;
import com.dream.six.vo.request.PermissionsRequestVO;
import com.dream.six.vo.request.RoleRequestVO;
import com.dream.six.vo.request.UpdatePermissionRequestVO;
import com.dream.six.vo.response.PermissionsResponseVO;
import com.dream.six.vo.response.RoleResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RoleService {

    void assignUserToRole(UUID userId, AssignRoleRequestVO request);

    void saveRole(RoleRequestVO request);

    ApiPageResponse<List<RoleResponseVO>> getAllRoles(int pageNumber, int pageSize);

    void savePermission(PermissionsRequestVO request);

    ApiPageResponse<Map<String,List<PermissionsResponseVO>>> getAllPermissions(String featureName, int pageNumber, int pageSize);

    void deletePermission(UUID permissionId);

    RoleResponseVO updateRole(UUID roleId, RoleRequestVO roleRequestVO);

    void deleteRole(UUID roleId);

    RoleResponseVO findRole(UUID roleId);

    RoleResponseVO getRoleByName(String roleName);

    PermissionsResponseVO updatePermission(UUID permissionId, UpdatePermissionRequestVO request);

    List<String> getFeatureNames();

    Boolean existsRoleByRoleName(String roleName);

    Boolean existsPermissionByPermissionName(String permissionName);

    Set<PermissionsResponseVO> getAllPermissionsByUserId(UUID id);

    void saveRolesFromCSV(MultipartFile file) throws IOException;

    void savePermissionsFromCSV(MultipartFile file);

    List<RoleResponseVO> findByListOfNamesAndIsDeletedFalse(List<String> roleNames);

    void exportRolePermissionToCsv(HttpServletResponse response, UUID roleId) throws IOException;
}
