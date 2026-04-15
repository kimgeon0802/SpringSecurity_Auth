package com.security.training.step1.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ========================================
 * [Step 1] JWT 인증 필터
 * ========================================
 * 
 * 모든 요청에서 'Authorization' 헤더를 확인하고, 유효한 JWT 토큰이 있으면
 * 해당 사용자를 인증된 사용자로 설정합니다.
 * 
 * 학습 포인트:
 * 1. Bearer Token: Authorization 헤더에서 토큰을 추출하는 표준 방식.
 * 2. SecurityContextHolder: 인증 정보를 보관하는 전역 저장소.
 * 3. OncePerRequestFilter: 요청당 한 번만 실행되는 필터.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. Authorization 헤더에서 Bearer 토큰 추출
            String jwt = parseJwt(request);

            // 2. 토큰이 있고 유효한 경우
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);

                // 3. 사용자 정보 로드 (UserDetailsService 활용)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. SecurityContext에 인증 정보 저장 (이후 필터들에서 인증된 것으로 판단)
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.info("🔐 JWT 인증 성공: {} -> SecurityContext에 저장됨", username);
            }
        } catch (Exception e) {
            log.error("❌ JWT 인증 처리 실패: {}", e.getMessage());
        }

        // 6. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 헤더에서 Bearer 토큰 추출
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // "Bearer " 이후의 토큰 값만 반환
        }

        return null;
    }
}
