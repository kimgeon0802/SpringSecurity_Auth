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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ========================================
 * [Step 3] 커스텀 AuthenticationEntryPoint
 * ========================================
 * 
 * ★ 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
 * 
 * 기본 동작: 브라우저에 401 상태 + WWW-Authenticate 헤더를 보내거나
 *           /login 페이지로 리다이렉트합니다.
 * 
 * 커스텀: HTML 리다이렉트 대신 401 JSON 응답을 내려줍니다.
 * 
 * [호출 시점]
 * - Authorization 헤더 없이 보호된 API 호출
 * - 만료된 토큰으로 접근
 * - 유효하지 않은 인증 정보
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("┌──────────────────────────────────────────────────");
        log.warn("│ [AuthenticationEntryPoint] 인증 실패 - 401");
        log.warn("│ URI    : {} {}", request.getMethod(), request.getRequestURI());
        log.warn("│ Error  : {}", authException.getClass().getSimpleName());
        log.warn("│ Message: {}", authException.getMessage());
        log.warn("└──────────────────────────────────────────────────");

        // ★ 에러 코드 분류
        String errorCode = resolveErrorCode(authException);

        ErrorResponse errorResponse = ErrorResponse.unauthorized(
                errorCode,
                authException.getMessage() != null ? authException.getMessage() : "인증이 필요합니다.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * ★ 예외 타입에 따른 에러 코드 매핑
     * → 프론트엔드에서 이 코드를 기반으로 UI 분기 처리
     */
    private String resolveErrorCode(AuthenticationException ex) {
        String exClassName = ex.getClass().getSimpleName();
        return switch (exClassName) {
            case "BadCredentialsException" -> "AUTH_001"; // 잘못된 인증정보
            case "DisabledException" ->       "AUTH_002"; // 비활성/휴면 계정
            case "LockedException" ->         "AUTH_003"; // 계정 잠김
            case "AccountExpiredException" -> "AUTH_004"; // 계정 만료
            case "InsufficientAuthenticationException" -> "AUTH_005"; // 인증 필요
            default ->                        "AUTH_999"; // 기타
        };
    }
}
