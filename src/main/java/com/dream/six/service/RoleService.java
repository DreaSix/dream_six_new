package com.dream.six.service;


import com.dream.six.vo.ApiPageResponse;
import com.dream.six.vo.request.AssignRoleRequestVO;
import com.dream.six.vo.request.RoleRequestVO;
import com.dream.six.vo.response.RoleResponseVO;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    void assignUserToRole(UUID userId, AssignRoleRequestVO request);

    void saveRole(RoleRequestVO request);

    ApiPageResponse<List<RoleResponseVO>> getAllRoles(int pageNumber, int pageSize);



    RoleResponseVO updateRole(UUID roleId, RoleRequestVO roleRequestVO);

    void deleteRole(UUID roleId);

    RoleResponseVO findRole(UUID roleId);

    RoleResponseVO getRoleByName(String roleName);


    Boolean existsRoleByRoleName(String roleName);


    List<RoleResponseVO> findByListOfNamesAndIsDeletedFalse(List<String> roleNames);

}
