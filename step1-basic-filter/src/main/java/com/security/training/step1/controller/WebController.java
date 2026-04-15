package com.security.training.step1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * [Step 1] 웹 페이지 컨트롤러
 * 
 * / 및 일반 경로 → webFilterChain (Order 2) 에서 처리
 * Form Login 인증 사용
 */
@Slf4j
@Controller
public class WebController {

    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> home() {
        log.info("✅ / 홈 페이지 - permitAll");
        return Map.of(
            "step", "Step 1 - Basic Filter",
            "description", "SecurityFilterChain 심화 분석 & DelegatingFilterProxy 실습",
            "endpoints", Map.of(
                "GET /api/public/health", "인증 불필요 (API 체인)",
                "GET /api/secured/data", "HTTP Basic 인증 필요 (API 체인)",
                "GET /dashboard", "Form Login 인증 필요 (Web 체인)",
                "GET /login", "로그인 페이지 (permitAll)"
            ),
            "credentials", Map.of(
                "user", "user / 1234 (ROLE_USER)",
                "admin", "admin / admin (ROLE_ADMIN)"
            )
        );
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginPage() {
        log.info("📝 로그인 페이지 요청");
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>Step 1 - JWT 실습</title>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; }
                    .container { max-width: 860px; margin: 40px auto; padding: 0 20px; }
                    h1 { font-size: 1.4rem; color: #333; margin-bottom: 24px; }
                    .card { background: white; border-radius: 8px; padding: 24px; margin-bottom: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
                    .card h2 { font-size: 1rem; color: #555; margin-bottom: 16px; border-bottom: 2px solid #4f8ef7; padding-bottom: 8px; }
                    input { width: 100%; padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem; margin-bottom: 10px; }
                    button { padding: 9px 20px; background: #4f8ef7; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 0.9rem; }
                    button:hover { background: #3a7de0; }
                    button.danger { background: #e74c3c; }
                    button.danger:hover { background: #c0392b; }
                    button.secondary { background: #6c757d; }
                    .result { margin-top: 14px; padding: 12px; background: #f8f9fa; border-radius: 4px; font-size: 0.82rem; font-family: monospace; white-space: pre-wrap; word-break: break-all; min-height: 40px; border-left: 3px solid #ddd; }
                    .result.success { border-left-color: #2ecc71; background: #f0fff4; }
                    .result.error   { border-left-color: #e74c3c; background: #fff5f5; }
                    .token-box { padding: 10px; background: #fff8e1; border: 1px solid #ffe082; border-radius: 4px; font-size: 0.78rem; font-family: monospace; word-break: break-all; margin-top: 10px; }
                    .step-label { display: inline-block; background: #4f8ef7; color: white; border-radius: 12px; padding: 2px 10px; font-size: 0.75rem; margin-bottom: 8px; }
                    .arrow { text-align: center; font-size: 1.5rem; color: #aaa; margin: -8px 0; }
                </style>
            </head>
            <body>
            <div class="container">
                <h1>🔐 Step 1 — JWT 인증 흐름 실습</h1>

                <!-- STEP 1: 로그인 -->
                <div class="card">
                    <span class="step-label">STEP 1</span>
                    <h2>로그인 → JWT 발급</h2>
                    <input id="username" value="user" placeholder="아이디" />
                    <input id="password" type="password" value="1234" placeholder="비밀번호" />
                    <button onclick="doLogin()">POST /api/login</button>
                    <div id="loginResult" class="result">결과가 여기에 표시됩니다.</div>
                    <div id="tokenBox" class="token-box" style="display:none"></div>
                </div>

                <div class="arrow">↓ 발급된 토큰을 localStorage에 저장 ↓</div>

                <!-- STEP 2: 토큰으로 API 호출 -->
                <div class="card">
                    <span class="step-label">STEP 2</span>
                    <h2>JWT 토큰으로 보호된 API 호출</h2>
                    <button onclick="callSecured()">GET /api/secured/data &nbsp;(토큰 O)</button>
                    &nbsp;
                    <button class="danger" onclick="callSecuredNoToken()">GET /api/secured/data &nbsp;(토큰 X)</button>
                    <div id="securedResult" class="result">결과가 여기에 표시됩니다.</div>
                </div>

                <div class="arrow">↓ 브라우저 개발자 도구 → Application → localStorage 확인 ↓</div>

                <!-- STEP 3: localStorage 비교 -->
                <div class="card">
                    <span class="step-label">STEP 3</span>
                    <h2>localStorage vs 세션 쿠키 비교</h2>
                    <button onclick="showStorage()">현재 localStorage 확인</button>
                    &nbsp;
                    <button class="secondary" onclick="clearStorage()">토큰 삭제 (로그아웃)</button>
                    <div id="storageResult" class="result">결과가 여기에 표시됩니다.</div>
                </div>
            </div>

            <script>
                const API = 'http://localhost:8081';

                // ── STEP 1: 로그인 ──────────────────────────────────────
                async function doLogin() {
                    const username = document.getElementById('username').value;
                    const password = document.getElementById('password').value;
                    const resultEl = document.getElementById('loginResult');
                    const tokenEl  = document.getElementById('tokenBox');

                    try {
                        const res = await fetch(`${API}/api/login`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ username, password })
                        });
                        const data = await res.json();

                        if (res.ok && data.accessToken) {
                            // ★ JWT를 localStorage에 저장 (SPA 방식)
                            localStorage.setItem('jwt_token', data.accessToken);

                            resultEl.className = 'result success';
                            resultEl.textContent =
                                `✅ 로그인 성공! (${res.status})\\n` +
                                `username : ${data.username}\\n` +
                                `tokenType: ${data.tokenType}`;

                            tokenEl.style.display = 'block';
                            tokenEl.textContent =
                                `🔑 발급된 토큰 (localStorage에 저장됨):\\n${data.accessToken}`;
                        } else {
                            resultEl.className = 'result error';
                            resultEl.textContent = `❌ 로그인 실패 (${res.status})\\n` + JSON.stringify(data, null, 2);
                            tokenEl.style.display = 'none';
                        }
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 네트워크 오류: ${e.message}`;
                    }
                }

                // ── STEP 2: 토큰 있음 ───────────────────────────────────
                async function callSecured() {
                    const token = localStorage.getItem('jwt_token');
                    const resultEl = document.getElementById('securedResult');

                    if (!token) {
                        resultEl.className = 'result error';
                        resultEl.textContent = '⚠️ 토큰 없음. 먼저 로그인하세요.';
                        return;
                    }

                    try {
                        const res = await fetch(`${API}/api/secured/data`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                            // ★ 쿠키가 아닌 Authorization 헤더로 전송!
                        });
                        const data = await res.json();
                        resultEl.className = 'result success';
                        resultEl.textContent =
                            `✅ 응답 (${res.status})\\n` + JSON.stringify(data, null, 2);
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 오류: ${e.message}`;
                    }
                }

                // ── STEP 2: 토큰 없음 (의도적 실패) ───────────────────
                async function callSecuredNoToken() {
                    const resultEl = document.getElementById('securedResult');
                    try {
                        const res = await fetch(`${API}/api/secured/data`);
                        // 헤더 없이 요청 → 서버가 막음
                        resultEl.className = 'result error';
                        resultEl.textContent =
                            `❌ 응답 (${res.status}) — Authorization 헤더 없이 접근 거부됨`;
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 오류: ${e.message}`;
                    }
                }

                // ── STEP 3: localStorage 확인 ──────────────────────────
                function showStorage() {
                    const resultEl = document.getElementById('storageResult');
                    const token = localStorage.getItem('jwt_token');
                    if (token) {
                        resultEl.className = 'result success';
                        resultEl.textContent =
                            `📦 localStorage['jwt_token'] 존재:\\n${token}\\n\\n` +
                            `👉 개발자도구 > Application > Local Storage > http://localhost:8081 에서도 확인 가능`;
                    } else {
                        resultEl.className = 'result error';
                        resultEl.textContent = '📭 localStorage에 토큰 없음 (로그아웃 상태)';
                    }
                }

                function clearStorage() {
                    localStorage.removeItem('jwt_token');
                    document.getElementById('tokenBox').style.display = 'none';
                    document.getElementById('storageResult').className = 'result';
                    document.getElementById('storageResult').textContent = '🗑️ 토큰 삭제 완료 → 로그아웃 상태';
                }
            </script>
            </body>
            </html>
            """;
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public Map<String, Object> dashboard(Authentication authentication) {
        log.info("🎯 대시보드 접근 - 사용자: {}", authentication.getName());
        return Map.of(
            "page", "Dashboard",
            "user", authentication.getName(),
            "roles", authentication.getAuthorities().toString(),
            "chain", "WEB-Chain (Form Login)"
        );
    }
}
