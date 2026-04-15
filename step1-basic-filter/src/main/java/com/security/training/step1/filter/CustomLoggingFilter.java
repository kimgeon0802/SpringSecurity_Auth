package com.security.training.step1.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ========================================
 * [Step 1] 커스텀 로깅 필터
 * ========================================
 * 
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행되는 필터.
 * 
 * 학습 포인트:
 * - OncePerRequestFilter vs GenericFilterBean 차이
 * - 필터 체인에서의 위치 확인 (Before/After)
 * - 체인 이름을 통해 어느 SecurityFilterChain을 타는지 확인
 */
@Slf4j
public class CustomLoggingFilter extends OncePerRequestFilter {

    private final String chainName;

    public CustomLoggingFilter(String chainName) {
        this.chainName = chainName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [{}] CustomLoggingFilter 진입", chainName);
        log.info("│ Method : {}", request.getMethod());
        log.info("│ URI    : {}", request.getRequestURI());
        log.info("│ Params : {}", request.getParameterMap().keySet());
        log.info("│ Auth   : {}", request.getHeader("Authorization") != null ? "있음" : "없음");
        log.info("│ Session: {}", request.getSession(false) != null ? request.getSession().getId() : "없음");
        log.info("└─────────────────────────────────────────────────────────");

        // ★ 다음 필터로 요청 전달 (이걸 호출하지 않으면 체인이 끊김!)
        filterChain.doFilter(request, response);

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [{}] CustomLoggingFilter 완료 - 응답 코드: {}", chainName, response.getStatus());
        log.info("└─────────────────────────────────────────────────────────");
    }
}
