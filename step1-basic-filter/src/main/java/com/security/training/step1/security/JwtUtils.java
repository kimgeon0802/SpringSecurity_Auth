package com.security.training.step1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ========================================
 * [Step 1] JWT 유틸리티 클래스
 * ========================================
 * 
 * JWT(JSON Web Token)의 생성, 파싱, 유효성 검증을 담당합니다.
 * 
 * 학습 포인트:
 * 1. Secret Key: 토큰의 무결성을 보장하는 핵심 키. (최소 256비트 이상 권장)
 * 2. Claims: 토큰에 담기는 정보 조각 (사용자명, 권한, 만료시간 등)
 * 3. Expiration: 토큰의 유효 기간 설정.
 */
@Slf4j
@Component
public class JwtUtils {

    // ★ 교육용 Secret Key (실무에서는 환경변수나 Secret Manager 사용 필수!)
    private final String SECRET_STRING = "spring-security-training-secret-key-2024-step-by-step";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    
    // 토큰 유효 시간: 1시간
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * JWT 토큰 생성
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("❌ 유효하지 않은 JWT 토큰: {}", e.getMessage());
        }
        return false;
    }
}
