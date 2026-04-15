package com.security.training.step2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ========================================
 * [Step 2] Custom Authentication
 * ========================================
 * 
 * 이 모듈에서 다루는 내용:
 * 
 * [13:00~15:00] AuthenticationManager & Provider
 * - CustomAuthenticationProvider: 휴면 계정 체크, 비밀번호 오류 횟수 제한
 * - ProviderManager: 다중 Provider 순회 과정 확인
 * - Authentication 객체의 라이프사이클
 * 
 * [15:00~17:00] JDBC 인증
 * - JPA 기반 UserDetailsService 구현
 * - H2 Database 사용 (schema.sql, data.sql 자동 실행)
 * - BCryptPasswordEncoder 적용
 * 
 * 테스트 계정 (비밀번호: 모두 1234):
 * - user         : 정상 계정 (ROLE_USER)
 * - admin        : 관리자 (ROLE_ADMIN)
 * - dormant_user : 휴면 계정 → 로그인 차단
 * - disabled_user: 비활성 계정 → 로그인 차단
 * - locked_user  : 비밀번호 5회 오류 → 로그인 차단
 * 
 * H2 Console: http://localhost:8082/h2-console
 * JDBC URL: jdbc:h2:mem:securitydb
 */
@SpringBootApplication
public class Step2Application {

    public static void main(String[] args) {
        SpringApplication.run(Step2Application.class, args);
    }
}
