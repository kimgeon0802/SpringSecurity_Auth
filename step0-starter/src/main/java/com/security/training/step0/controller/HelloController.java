package com.security.training.step0.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * [Step 0] 기본 컨트롤러
 * 
 * Spring Security 기본 설정 상태에서 이 엔드포인트에 접근하면
 * 자동으로 /login 페이지로 리다이렉트됩니다.
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "message", "Hello, Spring Security!",
            "description", "이 응답을 볼 수 있다면 인증에 성공한 것입니다."
        );
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "step", "Step 0 - Starter",
            "features", java.util.List.of(
                "Spring Boot 기본 설정",
                "Spring Security 자동 구성",
                "기본 로그인 페이지 (/login)",
                "모든 엔드포인트 인증 필요"
            )
        );
    }
}
