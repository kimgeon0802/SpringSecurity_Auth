package com.security.training.step1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ========================================
 * [Step 1] Basic Filter - 필터 체인 심화 분석
 * ========================================
 * 
 * 이 모듈에서 다루는 내용:
 * 
 * [09:00~11:00] Filter Chain 심화
 * - SecurityFilterChain의 전체 구조
 * - 각 필터의 역할과 호출 순서
 * - 커스텀 필터 추가 방법 (addFilterBefore/After)
 * - 특정 URL 패턴별 다른 SecurityFilterChain 적용
 * 
 * [11:00~12:00] DelegatingFilterProxy 실습
 * - 서블릿 컨테이너 ↔ 스프링 컨테이너 연결
 * - GenericFilterBean을 상속한 커스텀 필터
 * - SecurityFilterChain 빈 등록 vs 일반 빈 등록 비교
 * 
 * 디버깅 포인트:
 * - HttpFirewall
 * - SecurityContextPersistenceFilter
 * - UsernamePasswordAuthenticationFilter
 * - ExceptionTranslationFilter
 * - FilterSecurityInterceptor / AuthorizationFilter
 */
@SpringBootApplication
public class Step1Application {

    public static void main(String[] args) {
        SpringApplication.run(Step1Application.class, args);
    }
}
