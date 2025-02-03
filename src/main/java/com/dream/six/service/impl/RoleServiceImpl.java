package com.dream.six.service.impl;

import com.dream.six.constants.Constants;
import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.RoleEntity;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.enums.RoleEnum;
import com.dream.six.exception.InvalidFileNameException;
import com.dream.six.exception.InvalidInputException;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.exception.UserExistsException;
import com.dream.six.repository.RoleRepository;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.service.RoleService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.*;
import com.dream.six.vo.response.PermissionsResponseVO;
import com.dream.six.vo.response.RoleResponseVO;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;


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

                    return responseVO;
                })
                .toList();

        return ApiPageResponse.<List<RoleResponseVO>>builder().totalContent(responseVOS).totalCount(roleEntities.getTotalElements()).build();

    }


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
        return convertToRoleResponseVO(roleEntity);
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

        List<UserInfoEntity> existUser = userInfoRepository.findAllByIsDeletedFalse();
        existUser.forEach(userInfoEntity -> {
            List<RoleEntity> rolesToRemove = userInfoEntity.getRoles()
                    .stream()
                    .filter(role -> role.equals(roleEntity))
                    .toList();

            userInfoEntity.getRoles().removeAll(rolesToRemove);
        });


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
    public Boolean existsRoleByRoleName(String roleName) {
        log.info("Checking existence of Role with name: {}", roleName);
        return roleRepository.existsByNameAndIsDeletedFalse(roleName);
    }

    private RoleResponseVO convertToRoleResponseVO(RoleEntity roleEntity) {
        RoleResponseVO responseVO = new RoleResponseVO();
        responseVO.setId(roleEntity.getId());
        responseVO.setRoleName(roleEntity.getName());
        responseVO.setCreatedAt(mapper.convertDate(roleEntity.getCreatedAt()));
        responseVO.setUpdatedAt(mapper.convertDate(roleEntity.getUpdatedAt()));
        return responseVO;
    }



}
