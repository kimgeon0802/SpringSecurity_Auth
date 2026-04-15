package com.security.training.step1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * [Step 1] API 컨트롤러
 * 
 * /api/** 경로 → apiFilterChain (Order 1) 에서 처리
 * HTTP Basic 인증 사용
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/public/health")
    public Map<String, String> health() {
        log.info("✅ /api/public/health - 인증 없이 접근 가능 (permitAll)");
        return Map.of("status", "UP", "chain", "API-Chain", "auth", "불필요");
    }

    @GetMapping("/secured/data")
    public Map<String, Object> securedData() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("🔐 /api/secured/data - 인증된 사용자: {}", auth.getName());

        return Map.of(
            "message", "인증된 사용자만 볼 수 있는 데이터",
            "user", auth.getName(),
            "authorities", auth.getAuthorities().toString(),
            "chain", "API-Chain (HTTP Basic)"
        );
    }
}
