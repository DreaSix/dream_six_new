package com.dream.six.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RoleRequestVO {

    @NotBlank(message = "Role name is required")
    private String roleName;

    private List<UUID> permissionIds;

}
