package com.security.training.step1.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * ========================================
 * [Step 1] DelegatingFilterProxy 비교 실습용 필터
 * ========================================
 * 
 * ★ 핵심 실습: SecurityFilterChain에 등록한 필터 vs @Component일반 빈 등록 차이
 * 
 * 이 필터는 @Component로 등록되어 있어 서블릿 컨테이너가 직접 관리합니다.
 * → SecurityFilterChain과는 별도로 동작
 * → 실행 순서가 다름 (서블릿 필터 → 스프링 시큐리티 필터 순)
 * 
 * GenericFilterBean 상속:
 * - Spring 환경에서 필터를 구현할 때 사용하는 기본 클래스
 * - init() 시점에 Spring BeanFactory에 접근 가능
 * - DelegatingFilterProxy와 함께 사용되는 패턴
 * 
 * [비교 포인트]
 * 1. 이 필터(@Component)는 모든 요청에 실행됨 → SecurityFilterChain의 securityMatcher 무시
 * 2. SecurityFilterChain에 등록한 필터는 해당 체인이 매칭된 요청에서만 실행
 * 3. 로그 출력 순서를 통해 실행 시점 차이를 확인
 */
@Slf4j
@Component
public class ServletContainerManagedFilter extends GenericFilterBean {

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request,
                         jakarta.servlet.ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        log.info("═══════════════════════════════════════════════════════════");
        log.info("  [서블릿 컨테이너 필터] GenericFilterBean 실행");
        log.info("  URI: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("  ★ 이 필터는 @Component로 등록되어 SecurityFilterChain 밖에서 실행됩니다");
        log.info("  ★ DelegatingFilterProxy를 통하지 않고 서블릿 컨테이너가 직접 호출합니다");
        log.info("═══════════════════════════════════════════════════════════");

        chain.doFilter(request, response);
    }
}
