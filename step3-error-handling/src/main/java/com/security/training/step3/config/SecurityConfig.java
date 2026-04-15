package com.security.training.step3.config;

import com.security.training.step3.handler.CustomAccessDeniedHandler;
import com.security.training.step3.handler.CustomAuthenticationEntryPoint;
import com.security.training.step3.security.CustomAuthenticationProvider;
import com.security.training.step3.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * ========================================
 * [Step 3] SecurityConfig - REST API 에러 핸들링 적용
 * ========================================
 * 
 * ★ Step 2와의 차이점:
 * - .exceptionHandling()에 커스텀 핸들러 등록
 * - AuthenticationEntryPoint: 401 JSON 응답
 * - AccessDeniedHandler: 403 JSON 응답
 * 
 * 이것이 실무 SI/솔루션 프로젝트에서 가장 많이 사용하는 패턴입니다.
 */
@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        log.info("★★★ Step 3: AuthenticationManager 구성 (JWT + ErrorHandling 통합) ★★★");
        return new ProviderManager(List.of(customAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/demo", "/error", "/h2-console/**", "/api/public/**", "/api/login", "/api/test/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            // ★★★ Step 3 핵심: JWT 환경에서의 예외 처리 ★★★
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint) // 401: 유효하지 않은 토큰 등
                .accessDeniedHandler(accessDeniedHandler)           // 403: 관리자 권한 부족 등
            )
            .authenticationManager(authenticationManager())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
