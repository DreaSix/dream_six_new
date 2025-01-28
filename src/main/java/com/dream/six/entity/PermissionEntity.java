package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_PERMISSION_INFO")
public class PermissionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "FEATURE_NAME")
    private String featureName;

    @Column(name = "END_POINT_NAME")
    private String endPointName;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "ROUTE_PATH")
    private String routePath;

    @Column(name = "PERMISSION_NAME")
    private String permissionName;

    @Column(name = "KEY")
    private String key;
}
