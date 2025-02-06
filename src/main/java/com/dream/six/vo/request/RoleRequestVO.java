package com.dream.six.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RoleRequestVO {

    @NotBlank(message = "Role name is required")
    private String roleName;


}
