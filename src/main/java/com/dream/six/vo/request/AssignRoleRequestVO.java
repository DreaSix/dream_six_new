package com.dream.six.vo.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AssignRoleRequestVO {

    private List<UUID> roleIds;
}
