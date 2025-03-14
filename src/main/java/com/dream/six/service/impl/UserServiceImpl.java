package com.dream.six.service.impl;

import com.dream.six.constants.Constants;
import com.dream.six.constants.ErrorMessageConstants;
import com.dream.six.entity.RoleEntity;
import com.dream.six.entity.UserInfoEntity;
import com.dream.six.entity.WalletEntity;
import com.dream.six.enums.RoleEnum;
import com.dream.six.exception.InvalidInputException;
import com.dream.six.exception.ResourceNotFoundException;
import com.dream.six.exception.UserExistsException;
import com.dream.six.repository.RoleRepository;
import com.dream.six.repository.UserInfoRepository;
import com.dream.six.repository.WalletRepository;
import com.dream.six.service.RoleService;
import com.dream.six.service.UserService;
import com.dream.six.utils.PasswordUtils;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.AssignRoleRequestVO;
import com.dream.six.vo.request.ChangePasswordRequestVO;
import com.dream.six.vo.request.UserRequestVO;
import com.dream.six.vo.response.UserResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dream.six.mapper.CommonMapper.mapper;

@Service
@Slf4j

public class UserServiceImpl implements UserService {

    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;
    private final RoleService roleService;

    @Value("${super.admin.email}")
    private String email;

    public UserServiceImpl(UserInfoRepository userInfoRepository, RoleRepository roleRepository, RoleService roleService, WalletRepository walletRepository, WalletRepository walletRepository1) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.walletRepository = walletRepository1;
    }

    @Override
    @Transactional
    public UserResponseVO saveUser(UserRequestVO request) {
        log.info("save user: {}", request.getPhoneNumber());
        if (userInfoRepository.existsByPhoneNumberAndIsDeletedFalse(request.getPhoneNumber()))
            throw new UserExistsException(ErrorMessageConstants.USER_EMAIL_EXISTS);

        UserInfoEntity userInfo = new UserInfoEntity();

        userInfo.setName( request.getName() );
        userInfo.setPhoneNumber( request.getPhoneNumber() );
        userInfo.setUserName(request.getPhoneNumber());
        userInfo.setPassword(request.getPassword());
        userInfo.setEncodedPassword(PasswordUtils.hashPassword(request.getPassword()));

        //Check roles available or not.
        if (request.getRoles() != null) {
            List<RoleEntity> roleEntities = request.getRoles().stream()
                    .map(uuid -> roleRepository.findByIdAndIsDeletedFalse(uuid)
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + uuid)))
                    .toList();

            boolean rolesProvided = !roleEntities.isEmpty();

            if (rolesProvided) {
                log.info("Input roles for user: {}", request.getRoles());
                Boolean isRoot = roleEntities.stream().anyMatch(roleEntity -> roleEntity.getName().equals(RoleEnum.ROLE_SUPER_ADMIN.name()));
                if (Boolean.TRUE.equals(isRoot)) {
                    userInfo.setIsRoot(true);
                }
                userInfo.setRoles(roleEntities);
            } else {
                log.warn("No roles provided for the user .");
            }
        }
        UserInfoEntity userInfoEntity=userInfoRepository.save(userInfo);

        WalletEntity walletEntity = new WalletEntity();
                walletEntity.setCreatedByUUID(userInfoEntity.getId());
                walletRepository.save(walletEntity);

        log.info("User created successfully: {}", userInfo.getPhoneNumber());
        if (userInfo.getId() == null) {
            throw new ResourceNotFoundException(ErrorMessageConstants.USER_NOT_FOUND);
        }
        log.info("Authentication credentials generated for user: {}", userInfo.getPhoneNumber());
        return mapper.convertUserInfoEntityToUserResponse(userInfo);
    }

    @Override
    @Transactional
    public UserResponseVO updateUser(UUID userId, UserRequestVO request) {
        log.info("Updating user with ID: {}", userId);
        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, userId)));
        if (Boolean.TRUE.equals(userInfo.getIsRoot())) {
            throw new InvalidInputException(ErrorMessageConstants.SUPER_ADMIN_CHECK);
        }
        if (!userInfo.getPhoneNumber().equals(request.getPhoneNumber()) && (userInfoRepository.existsByPhoneNumberAndIsDeletedFalse(request.getPhoneNumber()))) {
            throw new UserExistsException(ErrorMessageConstants.USER_EMAIL_EXISTS);
        }
        UserInfoEntity newUserInfo = mapper.convertUserRequestToUserInfoEntity(request);
        newUserInfo.setId(userInfo.getId());
        newUserInfo.setCreatedAt(userInfo.getCreatedAt());
        newUserInfo.setUpdatedAt(userInfo.getUpdatedAt());
        newUserInfo.setRoles(userInfo.getRoles());

        userInfoRepository.save(newUserInfo);

        AssignRoleRequestVO assignRoleRequestVO = new AssignRoleRequestVO();
        assignRoleRequestVO.setRoleIds(request.getRoles());

        roleService.assignUserToRole(userInfo.getId(), assignRoleRequestVO);

        log.info("User updated successfully");

        return mapper.convertUserInfoEntityToUserResponse(newUserInfo);
    }

    @Override
    public UserResponseVO findUser(UUID userId) {
        log.info("Finding user with ID: {}", userId);

        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, userId)));

        log.info("User found successfully");

        Optional<WalletEntity> optionalWalletEntity = walletRepository.findByCreatedByUUID(userId);


        UserResponseVO userResponse = mapper.convertUserInfoEntityToUserResponse(userInfo);
        if(optionalWalletEntity.isPresent()){
            WalletEntity walletEntity = optionalWalletEntity.get();
            userResponse.setBalance(walletEntity.getBalance());
        }
        userResponse.setName(userInfo.getName());

        return userResponse;
    }

    @Override
    public ApiPageResponse<List<UserResponseVO>> getUsers(int pageNumber, int pageSize) {
        log.info("Retrieving users from page {} with size {}", pageNumber, pageSize);

        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Constants.CREATED_AT).descending());
        var userInfoPage = userInfoRepository.findAllByIsDeletedFalse(pageable);

        List<WalletEntity> walletEntities = walletRepository.findAll();

        var users = userInfoPage.getContent().stream()
                .map(item -> {
                    UserResponseVO userResponseVO = mapper.convertUserInfoEntityToUserResponse(item);
                    userResponseVO.setName(item.getName());

                    walletEntities.stream()
                            .filter(wallet -> wallet.getCreatedByUUID().equals(item.getId()))
                            .findFirst()
                            .ifPresent(wallet -> userResponseVO.setBalance(wallet.getBalance()));

                    return userResponseVO;
                })
                .toList();

        log.info("Retrieved {} users", users.size());

        return ApiPageResponse.<List<UserResponseVO>>builder().totalContent(users).totalCount(userInfoPage.getTotalElements()).build();
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, id)));

        if (Boolean.TRUE.equals(userInfo.getIsRoot())) {
            throw new InvalidInputException(ErrorMessageConstants.SUPER_ADMIN_CHECK);
        }

        log.info("User Info with ID {} deleted successfully (soft delete).", id);
    }

    @Override
    public void changePassword(ChangePasswordRequestVO requestVO, UUID userId) {
        UserInfoEntity userInfo = userInfoRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessageConstants.RESOURCE_WITH_ID_NOT_FOUND, ErrorMessageConstants.USER_NOT_FOUND, userId)));
        if (!userInfo.getPassword().equals(requestVO.getPassword())) {
            throw new InvalidInputException(ErrorMessageConstants.INVALID_CURRENT_PASSWORD);
        }
        String password = requestVO.getNewPassword();
        userInfo.setPassword(password);
        String newHashedPassword =PasswordUtils.hashPassword(password);
        userInfo.setEncodedPassword(newHashedPassword);
        userInfoRepository.save(userInfo);
    }

    @Override
    public List<UserResponseVO> findByRoleName(String roleName) {
        List<UserInfoEntity> userInfoEntities = userInfoRepository.findByRoles_NameOrderByCreatedAtDesc(roleName);

        List<UserResponseVO> users = userInfoEntities.stream()
                .map(mapper::convertUserInfoEntityToUserResponse)
                .toList();

        log.info("Retrieved {} users for role name: {}", users.size(), roleName);

        return users;
    }

    @Override
    public Map<String, List<UserResponseVO>> findByRoleNames(List<String> roleNames) {
        return roleNames.stream()
                .collect(Collectors.toMap(
                        roleName -> roleName,
                        this::findByRoleName
                ));
    }
}


