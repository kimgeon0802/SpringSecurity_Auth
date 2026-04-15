package com.security.training.step3.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ========================================
 * [Step 3] 통합 에러 응답 DTO
 * ========================================
 * 
 * ★ REST API에서 에러 응답 규격을 통일하는 것이 핵심!
 * 
 * 모든 인증/인가 에러는 이 형식으로 JSON 응답됩니다.
 * 
 * 응답 예시:
 * {
 *   "status": 401,
 *   "error": "Unauthorized",
 *   "code": "AUTH_001",
 *   "message": "인증이 필요합니다.",
 *   "path": "/api/secured/data",
 *   "timestamp": "2024-01-01T12:00:00"
 * }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP 상태 코드 */
    private int status;

    /** HTTP 상태 문자열 */
    private String error;

    /** 비즈니스 에러 코드 (프론트엔드에서 분기 처리용) */
    private String code;

    /** 사용자 친화적 에러 메시지 */
    private String message;

    /** 요청 URI */
    private String path;

    /** 에러 발생 시간 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // === 편의 팩토리 메서드 ===

    public static ErrorResponse unauthorized(String code, String message, String path) {
        return ErrorResponse.builder()
                .status(401)
                .error("Unauthorized")
                .code(code)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse forbidden(String code, String message, String path) {
        return ErrorResponse.builder()
                .status(403)
                .error("Forbidden")
                .code(code)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
