package com.dream.six.service.impl;

import com.dream.six.constants.Constants;
import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.PermissionEntity;
import com.dream.six.entity.RoleEntity;
import com.dream.six.entity.RolePermissionEntity;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.RoleEnum;
import com.dream.six.exception.InvalidFileNameException;
import com.dream.six.exception.InvalidInputException;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.exception.UserExistsException;
import com.dream.six.repository.PermissionRepository;
import com.dream.six.repository.RolePermissionRepository;
import com.dream.six.repository.RoleRepository;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.service.RoleService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.*;
import com.dream.six.vo.response.PermissionsResponseVO;
import com.dream.six.vo.response.RoleResponseVO;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.dream.six.mapper.CommonMapper.mapper;


@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Autowired
    public RoleServiceImpl(UserInfoRepository userInfoRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           RolePermissionRepository rolePermissionRepository) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Value("${featureName}")
    private String featureNameString;

    // @CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    @Override
    public void assignUserToRole(UUID userId, AssignRoleRequestVO request) {

        UserInfoEntity userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, userId)));

        List<RoleEntity> roleEntities = roleRepository.findAllByIdInDeletedFalse(request.getRoleIds());

        List<UUID> roleIds = request.getRoleIds();

        for (UUID roleId : roleIds) {
            boolean roleFound = roleEntities.stream()
                    .anyMatch(roleEntity -> roleEntity.getId().equals(roleId));
            if (!roleFound) {
                throw new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.ROLE_NOT_FOUND, roleId));
            }
        }

        userInfo.setRoles(roleEntities);
        userInfoRepository.save(userInfo);
    }

    //@CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    @Override
    public void saveRole(RoleRequestVO request) {
        if (roleRepository.existsByNameAndIsDeletedFalse(request.getRoleName()))
            throw new UserExistsException(ErrorMessageConstants.ROLE_NAME_EXISTS);
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
        roleEntity.setName(request.getRoleName());
        if (request.getRoleName().equals(RoleEnum.ROLE_SUPER_ADMIN.name())) {
            roleEntity.setIsRoot(true);
        }
        roleRepository.save(roleEntity);

        createRolePermissions(roleEntity, request.getPermissionIds());
    }

    private void createRolePermissions(RoleEntity roleEntity, List<UUID> permissionIds) {
        List<PermissionEntity> permissionEntities = permissionRepository.findAllByIdInDeletedFalse(permissionIds);
        List<RolePermissionEntity> rolePermissionEntities = permissionEntities.stream()
                .map(permissionEntity -> {
                    RolePermissionEntity rolePermission = new RolePermissionEntity();
                    rolePermission.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                    rolePermission.setRole(roleEntity);
                    rolePermission.setPermission(permissionEntity);
                    rolePermission.setRevoked(false);
                    return rolePermission;
                })
                .toList();
        rolePermissionRepository.saveAll(rolePermissionEntities);
    }


    @Override
    public ApiPageResponse<List<RoleResponseVO>> getAllRoles(int pageNumber, int pageSize) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Constants.CREATED_AT).descending());

        // Retrieve all non-deleted role entities
        var roleEntities = roleRepository.findAllByIsDeletedFalse(pageable);

        // Map role entities to role response VOs and set permissions
        var responseVOS = roleEntities.getContent().stream()
                .map(roleEntity -> {
                    RoleResponseVO responseVO = new RoleResponseVO();
                    responseVO.setId(roleEntity.getId());
                    responseVO.setCreatedAt(mapper.convertDate(roleEntity.getCreatedAt()));
                    responseVO.setUpdatedAt(mapper.convertDate(roleEntity.getUpdatedAt()));
                    responseVO.setRoleName(roleEntity.getName());
                    responseVO.setPermissions(rolePermissionRepository.findAllByRoleIdsInDeletedFalse(roleEntity.getId())
                            .stream()
                            .map(this::convertToPermissionResponseVO)
                            .toList());
                    return responseVO;
                })
                .toList();

        return ApiPageResponse.<List<RoleResponseVO>>builder().totalContent(responseVOS).totalCount(roleEntities.getTotalElements()).build();

    }

    @Override
    public void savePermission(PermissionsRequestVO request) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Constants.CREATED_AT).descending());

        List<PermissionEntity> existedPermissionEntities = permissionRepository.findAllByFeatureNameOrDeletedFalse(null, pageable).getContent();

        List<PermissionEntity> permissionEntities = request.getPermissionDetails()
                .stream()
                .filter(feature -> existedPermissionEntities.stream()
                        .noneMatch(entity -> entity.getFeatureName().equalsIgnoreCase(request.getFeatureName())
                                && entity.getType().equalsIgnoreCase(feature.getType())
                                && entity.getEndPointName().equalsIgnoreCase(feature.getEndPointName())
                                && entity.getRoutePath().equalsIgnoreCase(feature.getRoutePath())
                                && entity.getPermissionName().equalsIgnoreCase(feature.getPermissionName())
                                && entity.getKey().equalsIgnoreCase(feature.getKey())))
                .map(feature -> {
                    PermissionEntity permissionEntity = new PermissionEntity();
                    permissionEntity.setFeatureName(request.getFeatureName());
                    permissionEntity.setEndPointName(feature.getEndPointName());
                    permissionEntity.setRoutePath(feature.getRoutePath());
                    permissionEntity.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                    permissionEntity.setType(feature.getType());
                    permissionEntity.setPermissionName((feature.getPermissionName()));
                    permissionEntity.setKey(feature.getKey());
                    return permissionEntity;
                })
                .toList();

        permissionRepository.saveAll(permissionEntities);
    }

    @Override
    public ApiPageResponse<Map<String, List<PermissionsResponseVO>>> getAllPermissions(String featureName, int pageNumber, int pageSize) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Constants.CREATED_AT).descending());

        var permissions = permissionRepository.findAllByFeatureNameOrDeletedFalse(featureName, pageable);
        var permissionsResponseVOS = permissions.getContent().stream()
                .map(mapper::convertPermissionEntityToRsponse).collect(Collectors.groupingBy(PermissionsResponseVO::getFeatureName));

        return ApiPageResponse.<Map<String, List<PermissionsResponseVO>>>builder().totalContent(permissionsResponseVOS).totalCount(permissions.getTotalElements()).build();

    }

    @Transactional
    @Override
    //@CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    public void deletePermission(UUID permissionId) {

        PermissionEntity permissionEntity = permissionRepository.findByIdAndIsDeletedFalse(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.PERMISSION_NOT_FOUND, permissionId)));

        permissionEntity.setDeleted(true);
        permissionEntity.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        permissionRepository.save(permissionEntity);

        List<RolePermissionEntity> rolePermissionEntities = rolePermissionRepository.findAllByPermissionIdsInDeletedFalse(permissionEntity.getId());
        if (!rolePermissionEntities.isEmpty()) {
            rolePermissionEntities.forEach(rolePermission ->
                    rolePermission.setRevoked(true)
            );
        }

        log.info("User Info with ID {} deleted successfully (soft delete).", permissionId);

    }

    // @CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    @Override
    public RoleResponseVO updateRole(UUID roleId, RoleRequestVO roleRequestVO) {
        RoleEntity roleEntity = roleRepository.findByIdAndIsDeletedFalse(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.ROLE_NOT_FOUND, roleId)));

        if (Boolean.TRUE.equals(roleEntity.getIsRoot())) {
            throw new InvalidInputException(ErrorMessageConstants.SUPER_ADMIN_CHECK);
        }
        // Update role name
        if (!roleEntity.getName().equals(roleRequestVO.getRoleName()) && roleRepository.existsByNameAndIsDeletedFalse(roleRequestVO.getRoleName())) {
            throw new UserExistsException(ErrorMessageConstants.ROLE_NAME_EXISTS);
        }
        roleEntity.setName(roleRequestVO.getRoleName());
        roleEntity.setUpdatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
        roleRepository.save(roleEntity);
        // Revoke existing role permissions
        revokeExistingRolePermissions(roleId);
        // Save new role permissions
        createRolePermissions(roleEntity, roleRequestVO.getPermissionIds());
        // Return updated role response VO
        return convertToRoleResponseVO(roleEntity);
    }

    private void revokeExistingRolePermissions(UUID roleId) {
        List<RolePermissionEntity> existingRolePermissionEntities = rolePermissionRepository.findAllByRoleIdsInDeletedFalse(roleId);
        if (!existingRolePermissionEntities.isEmpty()) {
            existingRolePermissionEntities.forEach(token ->
                    token.setRevoked(true)
            );
            rolePermissionRepository.saveAll(existingRolePermissionEntities);
        }
    }

    private List<PermissionsResponseVO> getPermissionsForRole(UUID roleId) {
        return rolePermissionRepository.findAllByRoleIdsInDeletedFalse(roleId)
                .stream()
                .map(this::convertToPermissionResponseVO)
                .toList();
    }

    // @CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    @Transactional
    @Override
    public void deleteRole(UUID roleId) {
        RoleEntity roleEntity = roleRepository.findByIdAndIsDeletedFalse(roleId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.ROLE_NOT_FOUND, roleId)));

        if (Boolean.TRUE.equals(roleEntity.getIsRoot())) {
            throw new InvalidInputException(ErrorMessageConstants.SUPER_ADMIN_CHECK);
        }
        roleEntity.setDeleted(true);
        roleEntity.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        roleRepository.save(roleEntity);
        List<RolePermissionEntity> rolePermissionEntities = rolePermissionRepository.findAllByRoleIdsInDeletedFalse(roleId);

        List<UserInfoEntity> existUser = userInfoRepository.findAllByIsDeletedFalse();
        existUser.forEach(userInfoEntity -> {
            List<RoleEntity> rolesToRemove = userInfoEntity.getRoles()
                    .stream()
                    .filter(role -> role.equals(roleEntity))
                    .toList();

            userInfoEntity.getRoles().removeAll(rolesToRemove);
        });

        if (!rolePermissionEntities.isEmpty()) {
            rolePermissionEntities.forEach(rolePermission ->
                    rolePermission.setRevoked(true)
            );
        }

        log.info("Role with ID {} deleted successfully (soft delete).", roleId);
    }

    @Override
    public RoleResponseVO findRole(UUID roleId) {
        RoleEntity roleEntity = roleRepository.findByIdAndIsDeletedFalse(roleId).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.ROLE_NOT_FOUND, roleId)));

        return convertToRoleResponseVO(roleEntity);
    }

    @Override
    // @Cacheable(value = "ROLE-PERMISSIONS", key = "'allPermissions'")
    public RoleResponseVO getRoleByName(String roleName) {
        Optional<RoleEntity> roleEntity = roleRepository.findByNameAndIsDeletedFalse(roleName);
        return roleEntity.map(this::convertToRoleResponseVO).orElse(null);
    }

    @Override
    //@Cacheable(value = "ROLE-PERMISSIONS", key = "'allPermissions'")
    public List<RoleResponseVO> findByListOfNamesAndIsDeletedFalse(List<String> roleNames) {
        List<RoleEntity> roleEntities = roleRepository.findByListOfNamesAndIsDeletedFalse(roleNames);
        return roleEntities.stream().map(this::convertToRoleResponseVO).toList();
    }

    @Override
    public void exportRolePermissionToCsv(HttpServletResponse response, UUID roleId) throws IOException {
        // Fetch the role
        RoleResponseVO responseVO = findRole(roleId);
        // Generate the filename using the role name and suffix
        String fileName = responseVO.getRoleName() + "_permissions.csv";

        // Set the response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Generate and write the CSV file to the response
        try (PrintWriter writer = response.getWriter()) {
            generateCsvFile(responseVO, writer);
        }

        // Log success message
        System.out.println("CSV file generated successfully");
    }

    private void generateCsvFile(RoleResponseVO responseVO, Writer writer) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            // Write CSV header
            String[] header = {
                    "end_point_name", "feature_name", "route_path", "type", "permission_name", "key", "role_name"
            };
            csvWriter.writeNext(header);
            for (PermissionsResponseVO permissionsResponseVO : responseVO.getPermissions()) {
                String[] row = new String[]{
                        permissionsResponseVO.getEndPointName(), permissionsResponseVO.getFeatureName(),
                        permissionsResponseVO.getRoutePath(), permissionsResponseVO.getType(),
                        permissionsResponseVO.getPermissionName(), permissionsResponseVO.getKey(),
                        responseVO.getRoleName()
                };
                csvWriter.writeNext(row);
            }
        }
    }


    //@CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    @Override
    public PermissionsResponseVO updatePermission(UUID permissionId, UpdatePermissionRequestVO request) {
        PermissionEntity permissionEntity = permissionRepository.findByIdAndIsDeletedFalse(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.PERMISSION_NOT_FOUND, permissionId)));
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Constants.CREATED_AT).descending());

        List<PermissionEntity> existedPermissionEntities = permissionRepository.findAllByFeatureNameOrDeletedFalse(null, pageable).getContent();
        boolean exists = existedPermissionEntities.stream()
                .anyMatch(entity -> entity.getFeatureName().equalsIgnoreCase(request.getFeatureName())
                        && entity.getType().equalsIgnoreCase(request.getType())
                        && entity.getEndPointName().equalsIgnoreCase(request.getEndPointName())
                        && entity.getRoutePath().equalsIgnoreCase(request.getRoutePath())
                        && entity.getPermissionName().equalsIgnoreCase(request.getPermissionName())
                        && entity.getKey().equalsIgnoreCase(request.getKey()));

        if (exists) {
            throw new InvalidInputException(ErrorMessageConstants.DUPLICATE_PERMISSION_FOUND);
        }
        permissionEntity.setUpdatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
        permissionEntity.setPermissionName(request.getPermissionName());
        permissionEntity.setFeatureName(request.getFeatureName());
        permissionEntity.setEndPointName(request.getEndPointName());
        permissionEntity.setType(request.getType());
        permissionEntity.setKey(request.getKey());

        permissionRepository.save(permissionEntity);

        return mapper.convertPermissionEntityToRsponse(permissionEntity);
    }

    @Override
    public Boolean existsRoleByRoleName(String roleName) {
        log.info("Checking existence of Role with name: {}", roleName);
        return roleRepository.existsByNameAndIsDeletedFalse(roleName);
    }

    @Override
    public Boolean existsPermissionByPermissionName(String permissionName) {
        log.info("Checking existence of Role with name: {}", permissionName);
        return permissionRepository.existsByFeatureNameAndIsDeletedFalse(permissionName);
    }

    @Override
    public Set<PermissionsResponseVO> getAllPermissionsByUserId(UUID id) {
        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, id)));
        return userInfo.getRoles().stream()
                .flatMap(roleEntity -> getPermissionsForRole(roleEntity.getId()).stream())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    // @CacheEvict(cacheNames = "ROLE-PERMISSIONS", allEntries = true)
    public void saveRolesFromCSV(MultipartFile file) {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());

        if (originalFileName.contains(" ") || originalFileName.contains("(")) {
            throw new InvalidFileNameException("File name contains invalid characters: spaces or open brackets");
        }
        Map<String, List<RolePermissionRequestVO>> rolePermissionsMap = convertCsvToRolePermissionRequestVOs(file);

        rolePermissionsMap.forEach((roleName, rolePermissionRequestVOS) -> {

            if (!RoleEnum.ROLE_SUPER_ADMIN.toString().equals(roleName)) {
                RoleEntity roleEntity = roleRepository.findByNameAndIsDeletedFalse(roleName)
                        .orElseGet(() -> {
                            RoleEntity role = new RoleEntity();
                            role.setName(roleName);
                            if (roleName.equals(RoleEnum.ROLE_SUPER_ADMIN.name())) {
                                role.setIsRoot(true);
                            }
                            role.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                            return roleRepository.save(role);
                        });
                revokeExistingRolePermissions(roleEntity.getId());

                List<PermissionEntity> newPermissionList = new ArrayList<>();

                List<PermissionEntity> permissionEntities = rolePermissionRequestVOS.stream()
                        .map(rolePermissionRequestVO -> permissionRepository.findByFeatureNameAndEndPointNameAndTypeAndRoutePathAndPermissionNameAndKeyAndIsDeletedFalse(
                                        rolePermissionRequestVO.getFeatureName(),
                                        rolePermissionRequestVO.getEndPointName(),
                                        rolePermissionRequestVO.getType(),
                                        rolePermissionRequestVO.getRoutePath(),
                                        rolePermissionRequestVO.getPermissionName(),
                                        rolePermissionRequestVO.getKey())
                                .orElseGet(() -> {
                                    PermissionEntity newPermission = new PermissionEntity();
                                    newPermission.setFeatureName(rolePermissionRequestVO.getFeatureName());
                                    newPermission.setEndPointName(rolePermissionRequestVO.getEndPointName());
                                    newPermission.setType(rolePermissionRequestVO.getType());
                                    newPermission.setRoutePath(rolePermissionRequestVO.getRoutePath());
                                    newPermission.setPermissionName(rolePermissionRequestVO.getPermissionName());
                                    newPermission.setKey(rolePermissionRequestVO.getKey());
                                    newPermission.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                                    newPermissionList.add(newPermission);
                                    return newPermission;
                                })).toList();

                if (!newPermissionList.isEmpty()) {
                    permissionRepository.saveAll(newPermissionList);
                }
                List<RolePermissionEntity> rolePermissionEntities = permissionEntities.stream()
                        .map(permissionEntity -> {
                            RolePermissionEntity rolePermission = new RolePermissionEntity();
                            rolePermission.setRole(roleEntity);
                            rolePermission.setPermission(permissionEntity);
                            rolePermission.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                            rolePermission.setRevoked(false);
                            return rolePermission;
                        })
                        .toList();
                rolePermissionRepository.saveAll(rolePermissionEntities);
            }
        });

    }

    @Override
    public void savePermissionsFromCSV(MultipartFile file) {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());

        if (originalFileName.contains(" ") || originalFileName.contains("(")) {
            throw new InvalidFileNameException("File name contains invalid characters: spaces or open brackets");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            Set<PermissionEntity> permissionEntities = csvParser.getRecords().stream()
                    .map(csvRecord -> {
                        String featureName = csvRecord.get("feature_name");
                        String endPointName = csvRecord.get("end_point_name");
                        String type = csvRecord.get("type");
                        String routePath = csvRecord.get("route_path");
                        String permissionName = csvRecord.get("permission_name");
                        String key = csvRecord.get("key");

                        if (key == null || key.isEmpty() || featureName == null || featureName.isEmpty() ||
                                endPointName == null || endPointName.isEmpty() ||
                                type == null || type.isEmpty() ||
                                routePath == null || routePath.isEmpty() ||
                                permissionName == null || permissionName.isEmpty()) {
                            throw new InvalidInputException("Missing required fields in CSV csvRecord");
                        }

                        Optional<PermissionEntity> optionalPermissionEntity = permissionRepository.findByFeatureNameAndEndPointNameAndTypeAndRoutePathAndPermissionNameAndKeyAndIsDeletedFalse(
                                featureName,
                                endPointName,
                                type,
                                routePath,
                                permissionName,
                                key);

                        if (optionalPermissionEntity.isEmpty()) {
                            PermissionEntity permissionEntity = new PermissionEntity();
                            permissionEntity.setFeatureName(featureName);
                            permissionEntity.setType(type);
                            permissionEntity.setEndPointName(endPointName);
                            permissionEntity.setRoutePath(routePath);
                            permissionEntity.setCreatedBy(MDC.get(Constants.USERNAME_ATTRIBUTE));
                            permissionEntity.setPermissionName(permissionName);
                            permissionEntity.setKey(key);
                            return permissionEntity;
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!permissionEntities.isEmpty()) {
                permissionRepository.saveAll(permissionEntities);
            }

        } catch (IOException e) {
            throw new InvalidInputException("Invalid CSV file: " + e.getMessage());
        }
    }


    private Map<String, List<RolePermissionRequestVO>> convertCsvToRolePermissionRequestVOs(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {
            Set<RolePermissionRequestVO> rolePermissionRequestVOS = csvParser.getRecords().stream()
                    .map(csvRecord -> {
                        String roleName = csvRecord.get("role_name");
                        String featureName = csvRecord.get("feature_name");
                        String endPointName = csvRecord.get("end_point_name");
                        String type = csvRecord.get("type");
                        String routePath = csvRecord.get("route_path");
                        String permissionName = csvRecord.get("permission_name");
                        String key = csvRecord.get("key");

                        if (key == null || key.isEmpty() || roleName == null || roleName.isEmpty() ||
                                featureName == null || featureName.isEmpty() ||
                                endPointName == null || endPointName.isEmpty() ||
                                type == null || type.isEmpty() ||
                                routePath == null || routePath.isEmpty() ||
                                permissionName == null || permissionName.isEmpty()) {
                            throw new InvalidInputException("Missing required fields in CSV csvRecord");
                        }

                        RolePermissionRequestVO rolePermissionRequestVO = new RolePermissionRequestVO();
                        rolePermissionRequestVO.setRoleName(roleName);
                        rolePermissionRequestVO.setFeatureName(featureName);
                        rolePermissionRequestVO.setType(type);
                        rolePermissionRequestVO.setEndPointName(endPointName);
                        rolePermissionRequestVO.setRoutePath(routePath);
                        rolePermissionRequestVO.setPermissionName(permissionName);
                        rolePermissionRequestVO.setKey(key);
                        return rolePermissionRequestVO;
                    })
                    .collect(Collectors.toSet());

            // Group the list by Role Name using Stream API
            return rolePermissionRequestVOS.stream()
                    .collect(Collectors.groupingBy(RolePermissionRequestVO::getRoleName));
        } catch (IOException e) {
            throw new InvalidInputException("Invalid CSV file: " + e.getMessage());
        }
    }

    private RoleResponseVO convertToRoleResponseVO(RoleEntity roleEntity) {
        RoleResponseVO responseVO = new RoleResponseVO();
        responseVO.setId(roleEntity.getId());
        responseVO.setRoleName(roleEntity.getName());
        responseVO.setCreatedAt(mapper.convertDate(roleEntity.getCreatedAt()));
        responseVO.setUpdatedAt(mapper.convertDate(roleEntity.getUpdatedAt()));
        responseVO.setPermissions(getPermissionsForRole(roleEntity.getId()));
        return responseVO;
    }

    private PermissionsResponseVO convertToPermissionResponseVO(RolePermissionEntity rolePermission) {
        PermissionsResponseVO responseVO = new PermissionsResponseVO();
        responseVO.setId(rolePermission.getPermission().getId());
        responseVO.setFeatureName(rolePermission.getPermission().getFeatureName());
        responseVO.setEndPointName(rolePermission.getPermission().getEndPointName());
        responseVO.setType(rolePermission.getPermission().getType());
        responseVO.setRoutePath(rolePermission.getPermission().getRoutePath());
        responseVO.setPermissionName(rolePermission.getPermission().getPermissionName());
        responseVO.setKey(rolePermission.getPermission().getKey());
        responseVO.setCreatedAt(mapper.convertDate(rolePermission.getPermission().getCreatedAt()));
        responseVO.setUpdatedAt(mapper.convertDate(rolePermission.getPermission().getUpdatedAt()));
        return responseVO;
    }

    public List<String> getFeatureNames() {
        return Arrays.asList(featureNameString.split(","));
    }

}
