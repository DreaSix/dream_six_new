package com.dream.six.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_USER_AUTH")
public class UserAuthEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ENCODED_PASSWORD", nullable = false)
    private String encodedPassword;

    @Column(name = "USER_ID")
    private UUID userId;

    @OneToOne
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @JsonIgnoreProperties("userAuthEntity")
    private UserInfoEntity userInfo;

    @Override
    public String toString() {
        return "UserAuthEntity{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", encodedPassword='" + encodedPassword + '\'' +
                ", userId=" + userId +
                '}';
    }

}
