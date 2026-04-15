package com.security.training.step2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "step", "Step 2 - Custom AuthenticationProvider",
            "description", "DB(H2) 연동 + 커스텀 인증 Provider 실습",
            "hint", "자세한 정보는 /api/public/info 를 확인하세요"
        );
    }
}
