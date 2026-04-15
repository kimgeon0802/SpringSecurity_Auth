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
 * [Step 1] 요청 처리 시간 측정 필터
 * ========================================
 * 
 * 필터 체인의 맨 앞에 위치시켜 전체 처리 시간을 측정합니다.
 * 
 * addFilterBefore를 통해 CustomLoggingFilter보다 앞에 배치되므로
 * 필터 체인 내 순서를 직접 확인할 수 있습니다.
 */
@Slf4j
public class RequestTimingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String uri = request.getRequestURI();

        log.info("⏱️ [TimingFilter] 요청 시작: {} {}", request.getMethod(), uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("⏱️ [TimingFilter] 요청 완료: {} {} → {}ms (status: {})",
                    request.getMethod(), uri, elapsed, response.getStatus());
        }
    }
}
