package com.security.training.step3.controller;

import com.security.training.step3.repository.LoginHistoryRepository;
import com.security.training.step3.repository.UserRepository;
import com.security.training.step3.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [Step 3] API 컨트롤러
 * 
 * 테스트 시나리오:
 * 
 * 1. 인증 없이 접근 (401 JSON):
 *    curl http://localhost:8083/api/me
 * 
 * 2. 정상 인증 (200):
 *    curl -u user:1234 http://localhost:8083/api/me
 * 
 * 3. 권한 부족 (403 JSON):
 *    curl -u user:1234 http://localhost:8083/api/admin/users
 * 
 * 4. 휴면 계정 (401 JSON - 상세):
 *    curl -u dormant_user:1234 http://localhost:8083/api/me
 * 
 * 5. 잠긴 계정 (401 JSON - 상세):
 *    curl -u locked_user:1234 http://localhost:8083/api/me
 * 
 * 6. 틀린 비밀번호 (401 JSON - 횟수 포함):
 *    curl -u user:wrong http://localhost:8083/api/me
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
        result.put("step", "Step 3 - Error Handling");
        result.put("description", "REST API JSON 에러 응답 처리");
        result.put("테스트 시나리오", Map.of(
            "1. 인증 없이 접근", "curl http://localhost:8083/api/me → 401 JSON",
            "2. 정상 인증", "curl -u user:1234 http://localhost:8083/api/me → 200",
            "3. 권한 부족", "curl -u user:1234 http://localhost:8083/api/admin/users → 403 JSON",
            "4. 휴면 계정", "curl -u dormant_user:1234 http://localhost:8083/api/me → 401 JSON",
            "5. 잠긴 계정", "curl -u locked_user:1234 http://localhost:8083/api/me → 401 JSON",
            "6. 틀린 비밀번호", "curl -u user:wrong http://localhost:8083/api/me → 401 JSON"
        ));
        result.put("에러 코드 규격", Map.of(
            "AUTH_001", "잘못된 인증정보 (BadCredentials)",
            "AUTH_002", "비활성/휴면 계정 (Disabled)",
            "AUTH_003", "계정 잠김 (Locked)",
            "AUTH_005", "인증 필요 (Unauthenticated)",
            "AUTHZ_001", "권한 부족 (AccessDenied)"
        ));
        return result;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", userDetails.getUsername());
        result.put("email", userDetails.getUser().getEmail());
        result.put("authorities", userDetails.getAuthorities().toString());
        result.put("lastLogin", userDetails.getUser().getLastLogin());
        return result;
    }

    @GetMapping("/test/error")
    public Map<String, Object> testError() {
        throw new RuntimeException("컨트롤러 내부에서 강제 발생한 예외입니다.");
    }

    @GetMapping("/admin/users")
    public Object adminUsers() {
        log.info("🔐 [ADMIN] 전체 사용자 조회");
        return userRepository.findAll();
    }

    @GetMapping("/admin/login-history")
    public Object loginHistory() {
        log.info("🔐 [ADMIN] 로그인 이력 조회");
        return loginHistoryRepository.findAll();
    }
}
