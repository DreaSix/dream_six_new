package com.dream.six.vo.response;

import lombok.Data;

import java.util.UUID;


@Data
public class PermissionsResponseVO {

    private UUID id;
    private String featureName;
    private String endPointName;
    private String type;
    private String routePath;
    private String permissionName;
    private String key;
    private String createdAt;
    private String updatedAt;

}
