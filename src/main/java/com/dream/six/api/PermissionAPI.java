package com.dream.six.api;


import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.service.RoleService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.PermissionsRequestVO;
import com.dream.six.vo.request.UpdatePermissionRequestVO;
import com.dream.six.vo.response.PermissionsResponseVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/permissions", name = "permissions")
@Slf4j
public class PermissionAPI {

    private final RoleService roleService;

    @Autowired
    public PermissionAPI(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(value = "/create", name = "create permission", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> createPermission(@Valid @RequestBody PermissionsRequestVO request) {
        log.info("Received request to create Permissions.");
        log.info("Permissions creation request details: {}", request);
        roleService.savePermission(request);
        log.info("Permissions created successfully.");
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.PERMISSIONS_CREATED_SUCCESSFULLY)
                .message(ApiResponseMessages.PERMISSIONS_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{permission-id}/edit", name = "update permission", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<PermissionsResponseVO>> updatePermission(@PathVariable("permission-id") UUID permissionId, @Valid @RequestBody UpdatePermissionRequestVO request) {
        log.info("Received request to update permissions.");
        log.info("Update permissions request details: {}", request);
        PermissionsResponseVO responseVO = roleService.updatePermission(permissionId, request);
        log.info("Permissions updated successfully.");
        ApiResponse<PermissionsResponseVO> response = ApiResponse.<PermissionsResponseVO>builder()
                .data(responseVO)
                .message(ApiResponseMessages.PERMISSIONS_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/all", name = "get permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiPageResponse<Map<String,List<PermissionsResponseVO>>>> getAllPermissions(@RequestParam(required = false) String featureName,
                                                                                                      @RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                                                      @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        log.info("Received request to retrieve all Permissions.");
        var allPermissions = roleService.getAllPermissions(featureName,pageNumber,pageSize);
        allPermissions.setMessage(ApiResponseMessages.PERMISSIONS_FETCHED_SUCCESSFULLY);
        log.info("Retrieved all Permissions successfully.");

        return ResponseEntity.ok(allPermissions);
    }

    @DeleteMapping(value = "/{permission-id}/delete", name = "delete permission")
    public ResponseEntity<ApiResponse<String>> deletePermission(@PathVariable(name = "permission-id") UUID permissionId) {
        log.info("Received request to delete Permission with ID: {}", permissionId);

        roleService.deletePermission(permissionId);

        log.info("Permission with ID: {} deleted successfully", permissionId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.PERMISSIONS_DELETED_SUCCESSFULLY)
                .message(ApiResponseMessages.PERMISSIONS_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-feature-names")
    public ResponseEntity<ApiResponse<List<String>>> getFeatures() {
        log.info("Received request to get feature names");

        List<String> featureList = roleService.getFeatureNames();

        log.info("Retrieved feature names successfully");
        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .data(featureList)
                .message(ApiResponseMessages.FEATURE_NAMES_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/exists-permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Boolean>> checkPermissionExistenceByPermissionName(@RequestParam("permission-name") String permissionName) {
        log.info("Checking existence of Permission with name: {}", permissionName);

        var isPermissionExist = roleService.existsPermissionByPermissionName(permissionName);

        var message = String.format("Permission with name '%s' does not exist", permissionName);

        if (Boolean.TRUE.equals(isPermissionExist)) {
            message = String.format("Permission with name '%s' exists", permissionName);
        }
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .data(isPermissionExist)
                .message(message)
                .build();

        log.info(message);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/by-userId", name = "Retrieve Permissions by User ID", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Set<PermissionsResponseVO>>> getAllPermissionsByUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserInfoEntity userInfoEntity = (UserInfoEntity) authentication.getPrincipal();
//        log.info("Creating request for user: {}", userInfoEntity.g());
//        log.info("Initiating request to retrieve all permissions.");
//        Set<PermissionsResponseVO> allPermissions = roleService.getAllPermissionsByUserId(userInfoEntity.getId());
//        log.info("Successfully retrieved all permissions.");
//        ApiResponse<Set<PermissionsResponseVO>> response = ApiResponse.<Set<PermissionsResponseVO>>builder()
//                .data(allPermissions)
//                .message(ApiResponseMessages.PERMISSIONS_FETCHED_SUCCESSFULLY)
//                .build();
//
      return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> importPermissionsFromCSV(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received request to import roles from CSV.");
        log.info("File details: {}", file.getOriginalFilename());
        roleService.savePermissionsFromCSV(file);
        log.info("Permissions imported successfully.");
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.PERMISSIONS_IMPORTED_SUCCESSFULLY)
                .message(ApiResponseMessages.PERMISSIONS_IMPORTED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }
}
