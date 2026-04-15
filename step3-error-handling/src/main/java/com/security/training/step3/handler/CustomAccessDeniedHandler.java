package com.security.training.step3.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.training.step3.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ========================================
 * [Step 3] 커스텀 AccessDeniedHandler
 * ========================================
 * 
 * ★ 인증은 되었지만 권한이 부족할 때 호출됩니다.
 * 
 * 예: ROLE_USER가 ROLE_ADMIN 전용 API에 접근
 * 
 * 기본 동작: 403 에러 페이지 또는 /access-denied 리다이렉트
 * 커스텀: 403 JSON 응답 + 어떤 권한이 필요한지 명시
 * 
 * [호출 시점]
 * - 인증된 사용자가 .hasRole("ADMIN") 설정된 URL에 ROLE_USER로 접근
 * - @PreAuthorize 어노테이션에 의한 접근 거부
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        String authorities = auth != null ? auth.getAuthorities().toString() : "[]";

        log.warn("┌──────────────────────────────────────────────────");
        log.warn("│ [AccessDeniedHandler] 접근 거부 - 403");
        log.warn("│ URI        : {} {}", request.getMethod(), request.getRequestURI());
        log.warn("│ User       : {}", username);
        log.warn("│ Authorities: {}", authorities);
        log.warn("│ Error      : {}", accessDeniedException.getMessage());
        log.warn("└──────────────────────────────────────────────────");

        ErrorResponse errorResponse = ErrorResponse.forbidden(
                "AUTHZ_001",
                String.format("접근 권한이 없습니다. (현재 권한: %s)", authorities),
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
