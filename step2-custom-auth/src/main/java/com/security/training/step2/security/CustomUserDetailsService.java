package com.security.training.step2.security;

import com.security.training.step2.entity.Authority;
import com.security.training.step2.entity.User;
import com.security.training.step2.repository.AuthorityRepository;
import com.security.training.step2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================
 * [Step 2] JPA 기반 UserDetailsService 구현
 * ========================================
 * 
 * DB에서 사용자 정보를 조회하여 Spring Security가 인증에 사용할
 * UserDetails 객체를 생성합니다.
 * 
 * ★ 핵심 포인트:
 * - UserDetailsService는 "사용자를 찾는 것"만 담당
 * - 비밀번호 비교, 계정 검증은 AuthenticationProvider가 담당
 * - 책임 분리 (SRP) 원칙
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("┌──────────────────────────────────────────────");
        log.info("│ [UserDetailsService] 사용자 조회 시작: '{}'", username);

        // 1. DB에서 사용자 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("│ ❌ 사용자를 찾을 수 없음: '{}'", username);
                    log.info("└──────────────────────────────────────────────");
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        // 2. 권한 조회
        List<Authority> authorities = authorityRepository.findByUserId(user.getId());
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                .collect(Collectors.toList());

        log.info("│ ✅ 사용자 발견: {} (enabled={}, dormant={}, failCount={})",
                user.getUsername(), user.isEnabled(), user.isDormant(), user.getLoginFailCount());
        log.info("│ 🔑 권한: {}", grantedAuthorities);
        log.info("└──────────────────────────────────────────────");

        // 3. CustomUserDetails 객체 생성 (추가 정보 포함)
        return CustomUserDetails.builder()
                .user(user)
                .authorities(grantedAuthorities)
                .build();
    }
}
