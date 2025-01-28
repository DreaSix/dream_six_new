package com.dream.six.vo.request;

import lombok.Data;

@Data
public class RolePermissionRequestVO {

    private String roleName;
    private String featureName;
    private String type;
    private String endPointName;
    private String routePath;
    private String permissionName;
    private String key;
}
