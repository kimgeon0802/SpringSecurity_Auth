package com.security.training.step3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ========================================
 * [Step 3] Error Handling - REST API JSON 에러 응답
 * ========================================
 * 
 * 이 모듈에서 다루는 내용:
 * 
 * [17:00~18:00] Exception Handling
 * 
 * SI/솔루션 개발 시 가장 중요한 부분:
 * HTML 페이지 이동이 아닌 REST API 환경에서의 에러 처리
 * 
 * 1. CustomAuthenticationEntryPoint
 *    → 인증되지 않은 요청에 대해 401 JSON 응답
 * 
 * 2. CustomAccessDeniedHandler
 *    → 권한 부족 시 403 JSON 응답
 * 
 * 3. CustomAuthenticationFailureHandler
 *    → 로그인 실패 시 상세 JSON 에러 메시지
 * 
 * 4. ErrorResponse DTO
 *    → API 에러 응답 규격 통일
 * 
 * 테스트 시나리오:
 * - 인증 없이 접근      → 401 JSON
 * - 권한 부족           → 403 JSON
 * - 잘못된 비밀번호      → 401 JSON (상세 메시지)
 * - 휴면/잠김 계정       → 401 JSON (사유 포함)
 */
@SpringBootApplication
public class Step3Application {

    public static void main(String[] args) {
        SpringApplication.run(Step3Application.class, args);
    }
}
