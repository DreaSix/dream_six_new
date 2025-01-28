package com.dream.six.entity;

import com.dream.six.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_USER_TOKEN")
public class TokenEntity extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    @Column(name = "JWT_TOKEN", columnDefinition = "TEXT", nullable = false)
    private String jwtToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE", nullable = false)
    private TokenType tokenType = TokenType.BEARER;

    @Builder.Default
    @Column(name = "REVOKED", nullable = false)
    private boolean revoked = false;

    @Builder.Default
    @Column(name = "EXPIRED", nullable = false)
    private boolean expired = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserInfoEntity userInfo;
}
