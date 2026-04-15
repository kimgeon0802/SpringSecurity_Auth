package com.security.training.step3.security;

import com.security.training.step3.entity.LoginHistory;
import com.security.training.step3.repository.LoginHistoryRepository;
import com.security.training.step3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * [Step 3] CustomAuthenticationProvider
 * Step 2와 동일한 로직 + 예외 메시지를 더 구체적으로 전달
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

        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            saveHistory(username, false, "사용자 미존재");
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (!userDetails.isEnabled()) {
            saveHistory(username, false, "비활성 계정");
            throw new DisabledException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        if (userDetails.isDormant()) {
            saveHistory(username, false, "휴면 계정");
            throw new DisabledException("휴면 계정입니다. 휴면 해제 후 이용하세요.");
        }

        if (!userDetails.isAccountNonLocked()) {
            saveHistory(username, false, "계정 잠김");
            throw new LockedException("비밀번호 5회 오류로 계정이 잠겼습니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            int newCount = userDetails.getLoginFailCount() + 1;
            userRepository.findByUsername(username).ifPresent(u -> {
                u.setLoginFailCount(newCount);
                userRepository.save(u);
            });
            saveHistory(username, false, "비밀번호 불일치 (" + newCount + "회)");

            if (newCount >= 5) {
                throw new LockedException("비밀번호 5회 오류로 계정이 잠겼습니다.");
            }
            throw new BadCredentialsException("비밀번호가 올바르지 않습니다. (" + newCount + "/5회 실패)");
        }

        // 인증 성공
        userRepository.findByUsername(username).ifPresent(u -> {
            u.setLoginFailCount(0);
            u.setLastLogin(LocalDateTime.now());
            userRepository.save(u);
        });
        saveHistory(username, true, "인증 성공");

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private void saveHistory(String username, boolean success, String message) {
        loginHistoryRepository.save(LoginHistory.builder()
                .username(username).success(success).message(message)
                .createdAt(LocalDateTime.now()).build());
    }
}
