package com.dream.six.vo.response;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleResponseVO {

    private UUID id;
    private String roleName;
    private String createdAt;
    private String updatedAt;

}
