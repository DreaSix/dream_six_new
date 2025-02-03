package com.dream.six.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DS_USER_INFO")
public class UserInfoEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private UUID id;

    @Column(name = "NAME")
    private String Name;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;


    @Column(name = "IS_ROOT")
    private Boolean isRoot = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "DP_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @JsonIgnoreProperties("users")
    private List<RoleEntity> roles;


    @OneToOne(mappedBy = "userInfo", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("userInfo")
    private UserAuthEntity userAuthEntity;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private WalletEntity wallet;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roleNames = roles.stream().map(RoleEntity::getName).toList();

        return roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return userAuthEntity.getEncodedPassword();
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
