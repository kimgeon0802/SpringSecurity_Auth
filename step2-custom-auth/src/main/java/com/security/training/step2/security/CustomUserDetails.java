package com.security.training.step2.security;

import com.security.training.step2.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * ========================================
 * [Step 2] 커스텀 UserDetails 구현
 * ========================================
 * 
 * Spring Security의 기본 User 객체 대신 커스텀 UserDetails를 사용하여
 * 추가 정보(휴면 여부, 실패 횟수 등)를 인증 과정에 전달합니다.
 * 
 * ★ AuthenticationProvider에서 이 객체의 추가 필드를 활용하여
 *   비즈니스 로직 기반 인증 검증을 수행합니다.
 */
@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<? extends GrantedAuthority> authorities;

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
        return user.getUsername();
    }

    /**
     * 계정 잠김 여부
     * ★ 로그인 실패 5회 이상이면 잠김 처리
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getLoginFailCount() < 5;
    }

    /**
     * 계정 활성화 여부
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    /**
     * 계정 만료 여부 (여기서는 항상 유효)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부 (여기서는 항상 유효)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // === 추가 메서드: AuthenticationProvider에서 사용 ===

    /**
     * ★ 휴면 계정 여부 확인
     */
    public boolean isDormant() {
        return user.isDormant();
    }

    /**
     * 현재 로그인 실패 횟수
     */
    public int getLoginFailCount() {
        return user.getLoginFailCount();
    }
}
