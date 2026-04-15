package com.security.training.step0.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("★★★ Step 0: Session Mode SecurityFilterChain 구성 ★★★");

        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults()) // 기본 로그인 폼 사용
                .httpBasic(Customizer.withDefaults()); // curl 테스트를 위한 기본 인증 허용

        return http.build();
    }

    /**
     * 세션 생성/소멸 시 로그를 남기기 위한 리스너
     */
    @Bean
    public HttpSessionListener sessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                System.out.println("📢 [SESSION CREATED] New Session ID: " + se.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                System.out.println("📢 [SESSION DESTROYED] Session ID: " + se.getSession().getId());
            }
        };
    }
}
