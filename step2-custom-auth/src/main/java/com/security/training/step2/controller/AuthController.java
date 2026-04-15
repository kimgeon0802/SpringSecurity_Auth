package com.security.training.step2.controller;

import com.security.training.step2.security.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ========================================
 * [Step 2] 인증 컨트롤러 (JWT 통합)
 * ========================================
 * 
 * 1. 클라이언트가 ID/PW로 로그인 요청
 * 2. AuthenticationManager를 통해 CustomAuthenticationProvider 실행
 * 3. 인증 성공 시 JwtUtils를 통해 토근 발급 및 응답
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("🔑 [Step 2] 로그인 시도: {}", loginRequest.getUsername());

        try {
            // 1. AuthenticationManager를 통해 인증 시도
            // -> 내부적으로 CustomAuthenticationProvider가 실행되어 휴면/잠금/실패횟수 체크
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // 2. 인증 성공 시 토큰 생성
            String token = jwtUtils.generateToken(authentication.getName());

            log.info("✅ [Step 2] 로그인 성공, JWT 발급");
            return ResponseEntity.ok(Map.of(
                    "accessToken", token,
                    "tokenType", "Bearer",
                    "username", authentication.getName()
            ));

        } catch (Exception e) {
            log.error("❌ [Step 2] 로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of(
                    "error", "인증 실패",
                    "message", e.getMessage()
            ));
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
