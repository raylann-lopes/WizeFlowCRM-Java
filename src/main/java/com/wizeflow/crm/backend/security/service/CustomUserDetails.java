package com.wizeflow.crm.backend.security.service;

import com.wizeflow.crm.backend.infrastructure.entity.User;
import com.wizeflow.crm.backend.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Map account expiration from status if needed. Treat INACTIVE/LOCKED as non-expired; customize if you add EXPIRED status later.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        UserStatus status = user.getStatus();
        if (status == null) return true;
        return status != UserStatus.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        UserStatus status = user.getStatus();
        if (status == null) return true;
        return status == UserStatus.ACTIVE;
    }
}

