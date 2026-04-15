package com.security.training.step1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Slf4j
@Configuration
public class UserConfig {

    /**
     * PasswordEncoder 빈 등록
     * - BCrypt 해시 알고리즘 사용 (실무 표준)
     * - AuthController에서 비밀번호 검증 시 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * InMemory 사용자 설정 (교육용 하드코딩)
     * DB 없이 백엔드 코드에 사용자 정보를 직접 등록
     * Step 2에서 JPA + H2 DB 방식으로 전환 예정
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles("USER")
                .build();

        var admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN", "USER")
                .build();

        log.info("★ [Step 1] InMemory 사용자 등록 완료");
        log.info("  - user  / 1234  (ROLE_USER)");
        log.info("  - admin / admin (ROLE_ADMIN, ROLE_USER)");
        return new InMemoryUserDetailsManager(user, admin);
    }
}
