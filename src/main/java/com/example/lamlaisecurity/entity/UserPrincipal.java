package com.example.lamlaisecurity.entity;

import com.example.lamlaisecurity.config.constant.UserStatus;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPrincipal implements UserDetails {
    private String fullName;
    private String username;
    private String password;
    private UserStatus status;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal build(User user) {
        return UserPrincipal.builder()
                .fullName(user.getFullName())
                .username(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                        .toList())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
        boolean isLocked = true;

        if (status.equals(UserStatus.TEMPORARILY_LOCKED)) {
            isLocked = false;
        } else if (status.equals(UserStatus.LOCKED)) {
            isLocked = false;
        }

        return isLocked;
    }

    @Override
    public boolean isEnabled() {
        return !status.equals(UserStatus.WAITING_CONFIRM);
    }
}
