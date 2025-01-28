package com.dream.six.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseVO {
    private String accessToken;

    private String type = "Bearer";
    private String username;
    private UUID userId;
    private List<RoleDetail> roles;
    Set<PermissionsResponseVO> permissionsResponseVOS;

    private String refreshToken;

    public JwtResponseVO(String accessToken, String refreshToken, String username,UUID userId, List<RoleDetail> roles,Set<PermissionsResponseVO> permissionsResponseVOS) {
        this.accessToken = accessToken;
        this.username = username;
        this.userId = userId;
        this.roles = roles;
        this.refreshToken = refreshToken;
        this.permissionsResponseVOS = permissionsResponseVOS;
    }
}

