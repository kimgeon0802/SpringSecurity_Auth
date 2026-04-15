package com.security.training.step2.controller;

import com.security.training.step2.entity.LoginHistory;
import com.security.training.step2.repository.LoginHistoryRepository;
import com.security.training.step2.repository.UserRepository;
import com.security.training.step2.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [Step 2] API 컨트롤러
 * 
 * HTTP Basic 인증으로 테스트:
 * - curl -u user:1234 http://localhost:8082/api/me
 * - curl -u admin:1234 http://localhost:8082/api/admin/users
 * - curl -u dormant_user:1234 http://localhost:8082/api/me  → 실패 (휴면)
 * - curl -u locked_user:1234 http://localhost:8082/api/me   → 실패 (잠김)
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping("/public/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("step", "Step 2 - Custom Authentication");
        result.put("description", "커스텀 AuthenticationProvider + JDBC 인증");
        result.put("테스트 계정", Map.of(
            "user", "user / 1234 (정상, ROLE_USER)",
            "admin", "admin / 1234 (정상, ROLE_ADMIN)",
            "dormant_user", "dormant_user / 1234 (휴면 계정)",
            "disabled_user", "disabled_user / 1234 (비활성)",
            "locked_user", "locked_user / 1234 (5회 오류 잠금)"
        ));
        result.put("endpoints", Map.of(
            "GET /api/public/info", "인증 불필요",
            "GET /api/me", "인증 필요 - 내 정보",
            "GET /api/admin/users", "ADMIN 권한 필요",
            "GET /api/admin/login-history", "ADMIN 권한 필요 - 로그인 이력"
        ));
        return result;
    }

    /**
     * 내 정보 조회 (인증 필요)
     */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", userDetails.getUsername());
        result.put("email", userDetails.getUser().getEmail());
        result.put("authorities", userDetails.getAuthorities().toString());
        result.put("lastLogin", userDetails.getUser().getLastLogin());
        result.put("loginFailCount", userDetails.getLoginFailCount());
        return result;
    }

    /**
     * 전체 사용자 조회 (ADMIN 전용)
     */
    @GetMapping("/admin/users")
    public Object adminUsers() {
        log.info("🔐 [ADMIN] 전체 사용자 조회");
        return userRepository.findAll();
    }

    /**
     * 로그인 이력 조회 (ADMIN 전용)
     */
    @GetMapping("/admin/login-history")
    public List<LoginHistory> loginHistory() {
        log.info("🔐 [ADMIN] 로그인 이력 조회");
        return loginHistoryRepository.findAll();
    }
}
