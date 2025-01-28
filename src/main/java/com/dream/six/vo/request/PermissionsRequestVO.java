package com.dream.six.vo.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;


@Data
public class PermissionsRequestVO {

    @NotBlank(message = "Feature name is required")
    private String featureName;

    @NotEmpty(message = "Permission details are required")
    @Valid
    private List<PermissionDetails> permissionDetails;

    @Data
    public static class PermissionDetails {
        @NotBlank(message = "Type is required")
        private String type;
        @NotBlank(message = "End Point is required")
        private String endPointName;
        @NotBlank(message = "Route Path is required")
        private String routePath;
        @NotBlank(message = "Permission Name is required")
        private String permissionName;
        @NotBlank(message = "Key Name is required")
        private String key;
    }

    
}
