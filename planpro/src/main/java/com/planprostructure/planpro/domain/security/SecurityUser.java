package com.planprostructure.planpro.domain.security;

import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.StatusUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import lombok.Builder;

@Builder
public record SecurityUser(Users users) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()));
    }

    @Override
    public String getPassword() {
        return this.users.getPassword();
    }

    @Override
    public String getUsername() {
        return this.users.getUsername();
    }

    public Long getUserId() {
        return this.users.getId();
    }

    public String getEmail() {
        return this.users.getEmail();
    }

    public String getRole() {
        return this.users.getRole().name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return users.getStatus() != StatusUser.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return users.getStatus() != StatusUser.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return users.getStatus() != StatusUser.INACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return users.getStatus() == StatusUser.ACTIVE;
    }
}
