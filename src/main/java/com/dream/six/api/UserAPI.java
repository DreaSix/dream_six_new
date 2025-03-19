package com.dream.six.api;

import com.dream.six.constants.ApiResponseMessages;
import com.dream.six.constants.Constants;
import com.dream.six.service.UserService;
import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.ApiResponse;
import com.dream.six.vo.request.ChangePasswordRequestVO;
import com.dream.six.vo.request.UserRequestVO;
import com.dream.six.vo.response.UserResponseVO;
import com.dream.six.entity.UserInfoEntity;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserAPI {

    private final UserService userService;

    @Autowired
    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<String>> getHelloMessage() {
        String userUUID = MDC.get(Constants.USER_UUID_ATTRIBUTE);
        String username= MDC.get(Constants.USERNAME_ATTRIBUTE);
        System.out.println(username+" "+userUUID);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data("Hello,DemandPage!")
                .message("")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponseVO>> createUser(@Valid @RequestBody UserRequestVO request) {
        log.info("Received request to create a new user: {}", request);
        UserResponseVO responseDTO = userService.saveUser(request);
        log.info("New user created successfully: {}", responseDTO);
        ApiResponse<UserResponseVO> response = ApiResponse.<UserResponseVO>builder()
                .data(responseDTO)
                .message(ApiResponseMessages.USER_CREATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{user-id}/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponseVO>> updateUser(@PathVariable(name = "user-id") UUID userId, @RequestBody UserRequestVO request) {
        log.info("Received request to update user with ID {}: {}", userId, request);
        UserResponseVO updatedUser = userService.updateUser(userId, request);
        log.info("User with ID {} updated successfully. Updated user details: {}", userId, updatedUser);
        ApiResponse<UserResponseVO> response = ApiResponse.<UserResponseVO>builder()
                .data(updatedUser)
                .message(ApiResponseMessages.USER_UPDATED_SUCCESSFULLY)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{user-id}/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponseVO>> findUser(Authentication authentication,
                                                                @PathVariable(name = "user-id") UUID userId) {
        log.info("Received request to find user with ID {}", userId);
        UserResponseVO foundUser = userService.findUser(userId);

        log.info("User with ID {} found. User details: {}", userId, foundUser);
        ApiResponse<UserResponseVO> response = ApiResponse.<UserResponseVO>builder()
                .data(foundUser)
                .message(ApiResponseMessages.USER_FETCHED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiPageResponse<List<UserResponseVO>>> getUsers(@RequestParam(name = "page", defaultValue = "0") int pageNumber,
                                                                          @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        log.info("Received request to retrieve users. Page number: {}, Page size: {}", pageNumber, pageSize);
        var users = userService.getUsers(pageNumber, pageSize);

        users.setMessage(ApiResponseMessages.USER_FETCHED_SUCCESSFULLY);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping(value = "/{user-id}/delete")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable(name = "user-id") UUID userId) {
        log.info("Received request to delete user with ID: {}", userId);

        userService.deleteUser(userId);

        log.info("User with ID: {} deleted successfully", userId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .data(ApiResponseMessages.USER_DELETED_SUCCESSFULLY)
                .message(ApiResponseMessages.USER_DELETED_SUCCESSFULLY)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-users-by-roleNames")
    public ResponseEntity<Map<String, List<UserResponseVO>>> getUsersByRoleNames(@RequestParam String roleNames) {
        log.info("Request received to fetch users by role names: {}", roleNames);

        // Split the comma-separated role names into a list
        List<String> roleNameList = Arrays.asList(roleNames.split(","));

        Map<String, List<UserResponseVO>> usersByRole = userService.findByRoleNames(roleNameList);

        if (usersByRole.values().stream().allMatch(List::isEmpty)) {
            log.warn("No users found for role names: {}", roleNames);
            return ResponseEntity.noContent().build();
        }

        log.info("Retrieved users for role names: {}", roleNames);
        return ResponseEntity.ok(usersByRole);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword( @Valid @RequestBody ChangePasswordRequestVO requestVO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity userInfoEntity = (UserInfoEntity) authentication.getPrincipal();
        log.info("Received request to change password for user with ID: {}", userInfoEntity.getId());
        userService.changePassword(requestVO, userInfoEntity.getId());
        log.info("Password changed successfully for user with ID: {}", userInfoEntity.getId());
        return ResponseEntity.ok("Password changed successfully.");
    }

    @PutMapping("/forget-password")
    public ResponseEntity<String> forgetPassword( @RequestParam String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoEntity userInfoEntity = (UserInfoEntity) authentication.getPrincipal();
        log.info("Received request to forget password for user with ID: {}", userInfoEntity.getId());
        userService.forgetPassword(newPassword, userInfoEntity.getId());
        log.info("Password changed successfully for user with ID: {}", userInfoEntity.getId());
        return ResponseEntity.ok("Password changed successfully.");
    }



}
