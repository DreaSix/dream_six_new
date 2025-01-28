package com.dream.six.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePermissionRequestVO {

    @NotBlank(message = "Feature name is required")
    private String featureName;

    @NotBlank(message = "EndPoint is required")
    private String endPointName;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Route path is required")
    private String routePath;

    @NotBlank(message = "Permission name is required")
    private String permissionName;

    @NotBlank(message = "Key Name is required")
    private String key;
}
