package com.security.training.step2.security;

import com.security.training.step2.entity.LoginHistory;
import com.security.training.step2.repository.LoginHistoryRepository;
import com.security.training.step2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ========================================
 * [Step 2] 커스텀 AuthenticationProvider
 * ========================================
 * 
 * ★★★ 교육 하이라이트 ★★★
 * 
 * 단순 ID/PW 비교를 넘어 비즈니스 로직을 녹여낸 인증 처리:
 * 
 * 1. 휴면 계정 체크 → DisabledException
 * 2. 비밀번호 5회 오류 잠금 → LockedException
 * 3. 비활성 계정 체크 → DisabledException
 * 4. 비밀번호 검증 → BadCredentialsException
 * 5. 인증 성공 시 실패 횟수 초기화 + 마지막 로그인 시간 업데이트
 * 
 * [ProviderManager 동작 원리]
 * ProviderManager는 등록된 AuthenticationProvider 목록을 순회하면서
 * 각 Provider의 supports() 메서드로 처리 가능 여부를 확인합니다.
 * 
 * supports()가 true를 반환하면 authenticate()를 호출하고,
 * 인증 성공 시 즉시 반환, 실패 시 다음 Provider로 넘어갑니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        log.info("╔══════════════════════════════════════════════════════════");
        log.info("║ [CustomAuthenticationProvider] 인증 시작");
        log.info("║ Username: {}", username);
        log.info("╠══════════════════════════════════════════════════════════");

        // 1. 사용자 조회 (UserDetailsService 위임)
        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            log.error("║ ❌ 사용자 조회 실패: {}", e.getMessage());
            saveLoginHistory(username, false, "사용자를 찾을 수 없음");
            throw new BadCredentialsException("인증 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 2. ★ 비활성 계정 체크
        if (!userDetails.isEnabled()) {
            log.warn("║ 🚫 비활성 계정: {}", username);
            saveLoginHistory(username, false, "비활성 계정");
            log.info("╚══════════════════════════════════════════════════════════");
            throw new DisabledException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        // 3. ★ 휴면 계정 체크
        if (userDetails.isDormant()) {
            log.warn("║ 😴 휴면 계정: {} → 휴면 해제 후 이용 가능", username);
            saveLoginHistory(username, false, "휴면 계정");
            log.info("╚══════════════════════════════════════════════════════════");
            throw new DisabledException("휴면 계정입니다. 휴면 해제 후 이용하세요.");
        }

        // 4. ★ 비밀번호 오류 횟수 체크 (5회 이상 잠금)
        if (!userDetails.isAccountNonLocked()) {
            log.warn("║ 🔒 계정 잠김: {} (실패 횟수: {}회)", username, userDetails.getLoginFailCount());
            saveLoginHistory(username, false, "계정 잠김 (5회 오류)");
            log.info("╚══════════════════════════════════════════════════════════");
            throw new LockedException("비밀번호 5회 오류로 계정이 잠겼습니다. 관리자에게 문의하세요.");
        }

        // 5. ★ 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            // 실패 횟수 증가
            int newFailCount = userDetails.getLoginFailCount() + 1;
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setLoginFailCount(newFailCount);
                userRepository.save(user);
            });

            log.warn("║ ❌ 비밀번호 불일치: {} (실패 {}회 / 5회)", username, newFailCount);
            saveLoginHistory(username, false, "비밀번호 불일치 (" + newFailCount + "회)");
            log.info("╚══════════════════════════════════════════════════════════");

            if (newFailCount >= 5) {
                throw new LockedException("비밀번호 5회 오류로 계정이 잠겼습니다.");
            }
            throw new BadCredentialsException(
                    "비밀번호가 올바르지 않습니다. (" + newFailCount + "/5회 실패)");
        }

        // 6. ✅ 인증 성공! → 실패 횟수 초기화 + 마지막 로그인 시간 업데이트
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLoginFailCount(0);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });

        saveLoginHistory(username, true, "인증 성공");

        log.info("║ ✅ 인증 성공: {} (권한: {})", username, userDetails.getAuthorities());
        log.info("╚══════════════════════════════════════════════════════════");

        // 인증 완료된 토큰 반환 (credentials는 보안을 위해 null)
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    /**
     * ★ 이 Provider가 처리할 수 있는 Authentication 타입 지정
     * 
     * ProviderManager는 이 메서드를 호출하여
     * UsernamePasswordAuthenticationToken 타입인 경우에만 이 Provider를 사용합니다.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        boolean supported = UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        log.debug("[CustomAuthenticationProvider] supports({}) = {}",
                authentication.getSimpleName(), supported);
        return supported;
    }

    /**
     * 로그인 이력 저장
     */
    private void saveLoginHistory(String username, boolean success, String message) {
        loginHistoryRepository.save(LoginHistory.builder()
                .username(username)
                .success(success)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
