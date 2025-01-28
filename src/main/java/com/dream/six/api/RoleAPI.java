package com.dream.six.api;


import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.service.RoleService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.AssignRoleRequestVO;
import com.dream.six.vo.request.RoleRequestVO;
import com.dream.six.vo.response.RoleResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/role", name = "roles")
@Slf4j
public class RoleAPI {

    private final RoleService roleService;

    @Autowired
    public RoleAPI(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(value = "/{user-id}/assign", name = "assign role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> assignRole(@PathVariable(name = "user-id") UUID userId,
                                                          @RequestBody AssignRoleRequestVO request) {
        log.info("Received request to assign role for user with ID: {}", userId);
        log.info("Role assignment request details: {}", request);
        roleService.assignUserToRole(userId, request);
        log.info("Role assigned successfully for user with ID: {}", userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(String.format("%s with ID: %s", ApiResponseMessages.ROLES_ASSIGNED_SUCCESSFULLY, userId))
                .message(ApiResponseMessages.ROLES_ASSIGNED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create", name = "create role", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> createRole(@Valid @RequestBody RoleRequestVO request) {
        log.info("Received request to create  roles.");
        log.info("Role creation request details: {}", request);
        roleService.saveRole(request);
        log.info("Role created successfully.");
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.ROLES_CREATED_SUCCESSFULLY)
                .message(ApiResponseMessages.ROLES_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/all", name = "get roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiPageResponse<List<RoleResponseVO>>> getAllRoles(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                             @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        log.info("Received request to retrieve all roles.");
        var roles = roleService.getAllRoles(pageNumber, pageSize);
        roles.setMessage(ApiResponseMessages.ROLES_FETCHED_SUCCESSFULLY);
        log.info("Retrieved all roles successfully.");

        return ResponseEntity.ok(roles);
    }

    @PutMapping(value = "/{role-id}/update", name = "update role", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RoleResponseVO>> updateRole(@PathVariable("role-id") UUID roleId, @RequestBody RoleRequestVO roleRequestVO) {
        log.info("Received request to update  role.");
        RoleResponseVO role = roleService.updateRole(roleId, roleRequestVO);
        log.info("Role Updated successfully.");
        ApiResponse<RoleResponseVO> response = ApiResponse.<RoleResponseVO>builder()
                .data(role)
                .message(ApiResponseMessages.ROLES_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{role-id}/find", name = "find role", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RoleResponseVO>> findRole(@PathVariable("role-id") UUID roleId) {
        log.info("Received request to find Role with ID {}", roleId);
        RoleResponseVO role = roleService.findRole(roleId);
        log.info("Role with ID {} found. User details: {}", roleId, role);
        ApiResponse<RoleResponseVO> response = ApiResponse.<RoleResponseVO>builder()
                .data(role)
                .message(ApiResponseMessages.ROLES_FETCHED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{role-id}/delete", name = "delete role")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable("role-id") UUID roleId) {
        log.info("Received request to delete Role with ID: {}", roleId);
        roleService.deleteRole(roleId);
        log.info("Role with ID: {} deleted successfully", roleId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(String.format("%s with ID: %s", ApiResponseMessages.ROLES_ASSIGNED_SUCCESSFULLY, roleId))
                .message(ApiResponseMessages.ROLE_DELETED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/exists-role", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> checkRoleExistenceByRoleName(@RequestParam("role-name") String roleName) {
        log.info("Checking existence of Role with name: {}", roleName);

        Boolean isRoleExist = roleService.existsRoleByRoleName(roleName);

        String message;
        if (Boolean.TRUE.equals(isRoleExist)) {
            message = String.format("Role with name '%s' exists", roleName);
        } else {
            message = String.format("Role with name '%s' does not exist", roleName);
        }

        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .data(isRoleExist)
                .message(message)
                .build();

        log.info(message);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/exists-permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> checkPermissionExistenceByPermissionName(@RequestParam("permission-name") String permissionName) {
        log.info("Checking existence of Permission with name: {}", permissionName);

        Boolean isPermissionExist = roleService.existsPermissionByPermissionName(permissionName);

        String message;
        if (Boolean.TRUE.equals(isPermissionExist)) {
            message = String.format("Permission with name '%s' exists", permissionName);
        } else {
            message = String.format("Permission with name '%s' does not exist", permissionName);
        }

        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .data(isPermissionExist)
                .message(message)
                .build();

        log.info(message);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> importRolesFromCSV(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received request to import roles from CSV.");
        log.info("File details: {}", file.getOriginalFilename());
        roleService.saveRolesFromCSV(file);
        log.info("Roles imported successfully.");
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.ROLES_IMPORTED_SUCCESSFULLY)
                .message(ApiResponseMessages.ROLES_IMPORTED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/export")
    public void exportRolePermissionToCsv(HttpServletResponse response,
                                          @RequestParam UUID roleId) throws IOException {
        roleService.exportRolePermissionToCsv(response, roleId);
    }

}
