package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_ROLE_PERMISSION_INFO")
public class RolePermissionEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "PERMISSION_ID")
    private PermissionEntity permission;

    @Column(name = "REVOKED")
    private boolean revoked;

}
