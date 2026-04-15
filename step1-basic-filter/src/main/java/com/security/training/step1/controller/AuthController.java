package com.security.training.step1.controller;

import com.security.training.step1.security.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ========================================
 * [Step 1] 인증 컨트롤러 (인메모리 하드코딩)
 * ========================================
 *
 * DB 없이 백엔드 코드에 하드코딩된 사용자 정보로 인증합니다.
 * (UserConfig.java 의 InMemoryUserDetailsManager 사용)
 *
 * 인증 흐름:
 * 1. username 으로 InMemory 사용자 조회
 * 2. PasswordEncoder 로 비밀번호 일치 여부 확인
 * 3. 검증 통과 시 JWT 발급
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("🔑 로그인 요청: {}", loginRequest.getUsername());

        try {
            // 1. InMemory에서 사용자 조회 (없으면 UsernameNotFoundException)
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            // 2. 비밀번호 검증 (BCrypt 해시 비교)
            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                log.warn("❌ 비밀번호 불일치: {}", loginRequest.getUsername());
                return ResponseEntity.status(401)
                        .body(Map.of("error", "아이디 또는 비밀번호가 올바르지 않습니다."));
            }

            // 3. 검증 통과 → JWT 발급
            String token = jwtUtils.generateToken(userDetails.getUsername());

            log.info("✅ 로그인 성공: {} / 권한: {}", userDetails.getUsername(), userDetails.getAuthorities());
            return ResponseEntity.ok(Map.of(
                    "accessToken", token,
                    "tokenType", "Bearer",
                    "username", userDetails.getUsername(),
                    "roles", userDetails.getAuthorities().toString()
            ));

        } catch (UsernameNotFoundException e) {
            log.warn("❌ 존재하지 않는 사용자: {}", loginRequest.getUsername());
            return ResponseEntity.status(401)
                    .body(Map.of("error", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
