package com.security.training.step1.config;

import com.security.training.step1.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ========================================
 * [Step 1] SecurityConfig - 다중 필터 체인 설정
 * ========================================
 *
 * 핵심 학습 포인트:
 * 1. @Order를 사용한 다중 SecurityFilterChain
 *    - apiFilterChain  (Order 1): /api/**  → JWT + HTTP Basic, Stateless
 *    - webFilterChain  (Order 2): 그 외 모든 경로 → Form Login, Stateful
 * 2. addFilterBefore를 통한 커스텀 JWT 필터 삽입
 * 3. 요청 경로에 따라 어느 체인이 선택되는지 로그로 확인
 */
@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * [체인 1] API 전용 필터 체인 (Order 1 = 우선순위 높음)
     * 매칭 경로: /api/**
     * 인증 방식: JWT Bearer 토큰 (+ HTTP Basic 폴백)
     * 세션 정책: STATELESS
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        log.info("★★★ [Order 1] API FilterChain 구성 (/api/**) ★★★");

        http
            .securityMatcher("/api/**")  // ★ 이 체인은 /api/** 에만 적용
            .csrf(csrf -> csrf.disable())
            // 1. Stateless: 세션을 만들지 않음
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 2. 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/api/login").permitAll()
                .anyRequest().authenticated()
            )
            // 3. HTTP Basic 인증 활성화 (브라우저 팝업 / curl -u user:pass)
            .httpBasic(basic -> {})
            // 4. JWT 필터 등록 (HTTP Basic 필터 앞에 배치)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * [체인 2] 웹 페이지 전용 필터 체인 (Order 2 = 우선순위 낮음)
     * 매칭 경로: 나머지 모든 경로 (/, /login, /dashboard 등)
     * 인증 방식: Form Login (HTML 로그인 폼)
     * 세션 정책: 기본 (Stateful)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        log.info("★★★ [Order 2] Web FilterChain 구성 (그 외 경로) ★★★");

        http
            .csrf(csrf -> csrf.disable())
            // 1. 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login").permitAll()
                .anyRequest().authenticated()
            )
            // 2. Form Login 설정: /login GET → 우리가 만든 loginPage(), POST → 시큐리티 처리
            .formLogin(form -> form
                .loginPage("/login")          // 로그인 페이지 URL
                .loginProcessingUrl("/login") // POST 로그인 처리 URL
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            );

        return http.build();
    }
}
