package com.security.training.step0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ========================================
 * [Step 0] Spring Security Starter
 * ========================================
 * 
 * 이 모듈은 Spring Security의 기본 동작을 확인하기 위한 시작점입니다.
 * 
 * 확인 포인트:
 * 1. 아무 설정 없이도 모든 엔드포인트가 보호됨
 * 2. 콘솔에 자동 생성된 비밀번호 출력
 * 3. /login 기본 로그인 페이지 제공
 * 4. user / {generated-password}로 로그인 가능
 * 
 * 실행 후 확인:
 * - GET http://localhost:8080/api/hello → 302 Redirect to /login
 * - POST http://localhost:8080/login (form) → 인증 성공 후 원래 URL로 리다이렉트
 */
@SpringBootApplication
public class Step0Application {

    public static void main(String[] args) {
        SpringApplication.run(Step0Application.class, args);
    }
}
