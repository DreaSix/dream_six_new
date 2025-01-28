package com.dream.six.service;


import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.ChangePasswordRequestVO;
import com.dream.six.vo.request.UserRequestVO;
import com.dream.six.vo.response.UserResponseVO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {

    UserResponseVO saveUser(UserRequestVO request);

    UserResponseVO updateUser(UUID userId, UserRequestVO request);

    UserResponseVO findUser(UUID userId);

    ApiPageResponse<List<UserResponseVO>> getUsers(int pageNumber, int pageSize);

    void deleteUser(UUID userId);
    void changePassword(ChangePasswordRequestVO requestVO, UUID userId);

    List<UserResponseVO> findByRoleName(String roleName);

    Map<String, List<UserResponseVO>> findByRoleNames(List<String> roleNames);
}
