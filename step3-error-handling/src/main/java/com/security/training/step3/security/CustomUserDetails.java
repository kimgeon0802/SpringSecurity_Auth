package com.security.training.step3.security;

import com.security.training.step3.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private final User user;
    private final List<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return user.getPassword(); }
    @Override
    public String getUsername() { return user.getUsername(); }
    @Override
    public boolean isAccountNonLocked() { return user.getLoginFailCount() < 5; }
    @Override
    public boolean isEnabled() { return user.isEnabled(); }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    public boolean isDormant() { return user.isDormant(); }
    public int getLoginFailCount() { return user.getLoginFailCount(); }
}
