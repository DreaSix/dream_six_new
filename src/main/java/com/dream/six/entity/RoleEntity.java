package com.dream.six.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_ROLE_INFO")
public class RoleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    //@Enumerated(EnumType.STRING)
    @Column(name = "NAME")
    private String name;

    @Column(name = "IS_ROOT")
    private Boolean isRoot = false;

}
