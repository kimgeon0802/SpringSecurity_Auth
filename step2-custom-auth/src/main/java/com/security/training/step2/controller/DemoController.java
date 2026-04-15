package com.security.training.step2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public String demo() {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>Step 2 - Custom AuthenticationProvider 실습</title>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; }
                    .container { max-width: 960px; margin: 40px auto; padding: 0 20px; }
                    h1 { font-size: 1.4rem; color: #333; margin-bottom: 8px; }
                    .subtitle { color: #888; font-size: 0.9rem; margin-bottom: 24px; }
                    .card { background: white; border-radius: 8px; padding: 24px; margin-bottom: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
                    .card h2 { font-size: 1rem; color: #444; margin-bottom: 16px; border-bottom: 2px solid #4f8ef7; padding-bottom: 8px; }
                    .step-label { display: inline-block; background: #4f8ef7; color: white; border-radius: 12px; padding: 2px 10px; font-size: 0.75rem; margin-bottom: 8px; }
                    input { padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem; margin-right: 8px; width: 160px; }
                    .btn-row { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
                    button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.82rem; color: white; }
                    .btn-primary  { background: #4f8ef7; }
                    .btn-success  { background: #2ecc71; }
                    .btn-warning  { background: #e67e22; }
                    .btn-danger   { background: #e74c3c; }
                    .btn-dark     { background: #6c757d; }
                    button:hover  { opacity: 0.85; }
                    .result { margin-top: 14px; padding: 12px; background: #f8f9fa; border-radius: 4px; font-size: 0.82rem; font-family: monospace; white-space: pre-wrap; word-break: break-all; min-height: 40px; border-left: 3px solid #ddd; }
                    .result.success { border-left-color: #2ecc71; background: #f0fff4; }
                    .result.error   { border-left-color: #e74c3c; background: #fff5f5; }
                    .result.warn    { border-left-color: #e67e22; background: #fff9f0; }
                    .account-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
                    .account-table th { background: #4f8ef7; color: white; padding: 8px 12px; text-align: left; }
                    .account-table td { padding: 8px 12px; border-bottom: 1px solid #eee; }
                    .account-table tr:last-child td { border-bottom: none; }
                    .badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 0.75rem; }
                    .badge-ok      { background: #d4edda; color: #155724; }
                    .badge-dormant { background: #fff3cd; color: #856404; }
                    .badge-lock    { background: #f8d7da; color: #721c24; }
                    .badge-off     { background: #d6d8d9; color: #383d41; }
                    .token-box { padding: 10px; background: #fff8e1; border: 1px solid #ffe082; border-radius: 4px; font-size: 0.78rem; font-family: monospace; word-break: break-all; margin-top: 10px; display: none; }
                    .flow-box { background: #f8f9fa; border-radius: 6px; padding: 16px; font-family: monospace; font-size: 0.82rem; line-height: 1.8; }
                    a { color: #4f8ef7; }
                </style>
            </head>
            <body>
            <div class="container">
                <h1>🔐 Step 2 — Custom AuthenticationProvider 실습</h1>
                <p class="subtitle">DB(H2) 연동 + 비즈니스 인증 규칙 (휴면/잠금/비활성) 시연</p>

                <!-- 계정 목록 -->
                <div class="card">
                    <span class="step-label">테스트 계정</span>
                    <h2>data.sql에 등록된 계정 목록</h2>
                    <table class="account-table">
                        <tr>
                            <th>계정</th><th>비밀번호</th><th>상태</th><th>예상 결과</th>
                        </tr>
                        <tr>
                            <td>user</td><td>1234</td>
                            <td><span class="badge badge-ok">정상</span></td>
                            <td>✅ 로그인 성공 → JWT 발급</td>
                        </tr>
                        <tr>
                            <td>admin</td><td>1234</td>
                            <td><span class="badge badge-ok">정상 (ADMIN)</span></td>
                            <td>✅ 로그인 성공 → JWT 발급</td>
                        </tr>
                        <tr>
                            <td>dormant_user</td><td>1234</td>
                            <td><span class="badge badge-dormant">휴면</span></td>
                            <td>❌ DisabledException</td>
                        </tr>
                        <tr>
                            <td>disabled_user</td><td>1234</td>
                            <td><span class="badge badge-off">비활성</span></td>
                            <td>❌ DisabledException</td>
                        </tr>
                        <tr>
                            <td>locked_user</td><td>1234</td>
                            <td><span class="badge badge-lock">잠금 (5회)</span></td>
                            <td>❌ LockedException</td>
                        </tr>
                    </table>
                </div>

                <!-- STEP 1: 계정별 로그인 시나리오 -->
                <div class="card">
                    <span class="step-label">STEP 1</span>
                    <h2>계정별 로그인 시도 → Provider 동작 확인</h2>
                    <input id="username" placeholder="아이디" value="user" />
                    <input id="password" type="password" placeholder="비밀번호" value="1234" />
                    <div class="btn-row">
                        <button class="btn-success" onclick="doLogin('user','1234')">✅ user / 1234 (정상)</button>
                        <button class="btn-success" onclick="doLogin('admin','1234')">✅ admin / 1234 (ADMIN)</button>
                        <button class="btn-warning" onclick="doLogin('dormant_user','1234')">😴 휴면 계정</button>
                        <button class="btn-dark"    onclick="doLogin('disabled_user','1234')">🚫 비활성 계정</button>
                        <button class="btn-danger"  onclick="doLogin('locked_user','1234')">🔒 잠금 계정</button>
                        <button class="btn-danger"  onclick="doLogin('user','wrong')">❌ 비밀번호 오류</button>
                    </div>
                    <div class="btn-row">
                        <button class="btn-primary" onclick="doLoginCustom()">직접 입력 로그인</button>
                    </div>
                    <div id="loginResult" class="result">계정 버튼을 클릭하면 결과가 여기에 표시됩니다.</div>
                    <div id="tokenBox" class="token-box"></div>
                </div>

                <!-- STEP 2: 토큰으로 API 호출 -->
                <div class="card">
                    <span class="step-label">STEP 2</span>
                    <h2>JWT 토큰으로 API 호출</h2>
                    <div class="btn-row">
                        <button class="btn-primary" onclick="callApi('/api/me')">GET /api/me (내 정보)</button>
                        <button class="btn-warning" onclick="callApi('/api/admin/users')">GET /api/admin/users (ADMIN 전용)</button>
                        <button class="btn-dark"    onclick="callApi('/api/admin/login-history')">GET /api/admin/login-history</button>
                    </div>
                    <div id="apiResult" class="result">로그인 후 API를 호출해보세요.</div>
                </div>

                <!-- STEP 3: H2 콘솔 + 로그인 이력 -->
                <div class="card">
                    <span class="step-label">STEP 3</span>
                    <h2>DB 직접 확인 — H2 콘솔</h2>
                    <p style="margin-bottom:12px; font-size:0.9rem; color:#555;">
                        <a href="/h2-console" target="_blank">👉 H2 콘솔 열기</a>
                        &nbsp;(JDBC URL: <code>jdbc:h2:mem:securitydb</code> / SA / 비밀번호 없음)
                    </p>
                    <div class="flow-box">
-- 로그인 이력 조회<br>
SELECT * FROM LOGIN_HISTORY ORDER BY CREATED_AT DESC;<br><br>
-- 실패 횟수 확인<br>
SELECT USERNAME, LOGIN_FAIL_COUNT, ENABLED, DORMANT FROM USERS;<br><br>
-- locked_user 잠금 해제 (실습용)<br>
UPDATE USERS SET LOGIN_FAIL_COUNT = 0 WHERE USERNAME = 'locked_user';
                    </div>
                    <div class="btn-row" style="margin-top:12px;">
                        <button class="btn-primary" onclick="callApi('/api/admin/login-history')">로그인 이력 API로 조회</button>
                    </div>
                    <div id="historyResult" class="result">버튼을 클릭하면 이력이 표시됩니다.</div>
                </div>

                <!-- 인증 흐름 설명 -->
                <div class="card">
                    <span class="step-label">인증 흐름</span>
                    <h2>CustomAuthenticationProvider 처리 순서</h2>
                    <div class="flow-box">
POST /api/login (username, password)<br>
&nbsp;&nbsp;&nbsp;&nbsp;↓ AuthenticationManager.authenticate()<br>
&nbsp;&nbsp;&nbsp;&nbsp;↓ ProviderManager → CustomAuthenticationProvider.authenticate()<br>
&nbsp;&nbsp;&nbsp;&nbsp;↓<br>
&nbsp;&nbsp;&nbsp;&nbsp;① CustomUserDetailsService.loadUserByUsername() → DB 조회<br>
&nbsp;&nbsp;&nbsp;&nbsp;② isEnabled() 체크 → false면 DisabledException<br>
&nbsp;&nbsp;&nbsp;&nbsp;③ isDormant() 체크 → true면 DisabledException<br>
&nbsp;&nbsp;&nbsp;&nbsp;④ isAccountNonLocked() 체크 → 잠금이면 LockedException<br>
&nbsp;&nbsp;&nbsp;&nbsp;⑤ passwordEncoder.matches() → 불일치면 실패횟수+1 후 BadCredentialsException<br>
&nbsp;&nbsp;&nbsp;&nbsp;↓ 모두 통과<br>
&nbsp;&nbsp;&nbsp;&nbsp;⑥ 실패횟수 초기화 + lastLogin 업데이트 (DB UPDATE)<br>
&nbsp;&nbsp;&nbsp;&nbsp;⑦ JWT 발급 → 응답
                    </div>
                </div>
            </div>

            <script>
                const API = 'http://localhost:8082';

                async function doLogin(username, password) {
                    document.getElementById('username').value = username;
                    document.getElementById('password').value = password;
                    await login(username, password);
                }

                async function doLoginCustom() {
                    const username = document.getElementById('username').value;
                    const password = document.getElementById('password').value;
                    await login(username, password);
                }

                async function login(username, password) {
                    const resultEl = document.getElementById('loginResult');
                    const tokenEl  = document.getElementById('tokenBox');
                    resultEl.className = 'result';
                    resultEl.textContent = '요청 중...';

                    try {
                        const res = await fetch(`${API}/api/login`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ username, password })
                        });
                        const data = await res.json();

                        if (res.ok) {
                            localStorage.setItem('jwt_token', data.accessToken);
                            resultEl.className = 'result success';
                            resultEl.textContent =
                                `✅ 로그인 성공 (200)\\n` +
                                `username  : ${data.username}\\n` +
                                `tokenType : ${data.tokenType}\\n\\n` +
                                `→ localStorage['jwt_token'] 에 저장 완료`;
                            tokenEl.style.display = 'block';
                            tokenEl.textContent = `🔑 발급된 토큰:\\n${data.accessToken}`;
                        } else {
                            localStorage.removeItem('jwt_token');
                            resultEl.className = 'result error';
                            resultEl.textContent =
                                `❌ 로그인 실패 (${res.status})\\n` +
                                `error   : ${data.error}\\n` +
                                `message : ${data.message}`;
                            tokenEl.style.display = 'none';
                        }
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 네트워크 오류: ${e.message}`;
                    }
                }

                async function callApi(path) {
                    const token = localStorage.getItem('jwt_token');
                    const isHistory = path.includes('login-history');
                    const resultEl = document.getElementById(isHistory ? 'historyResult' : 'apiResult');
                    resultEl.className = 'result';
                    resultEl.textContent = '요청 중...';

                    if (!token) {
                        resultEl.className = 'result warn';
                        resultEl.textContent = '⚠️ 토큰 없음. 먼저 로그인하세요.';
                        return;
                    }

                    try {
                        const res = await fetch(`${API}${path}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const data = await res.json();

                        if (res.ok) {
                            resultEl.className = 'result success';
                            resultEl.textContent = `✅ ${res.status} OK\\n` + JSON.stringify(data, null, 2);
                        } else {
                            resultEl.className = 'result error';
                            resultEl.textContent = `❌ ${res.status}\\n` + JSON.stringify(data, null, 2);
                        }
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 오류: ${e.message}`;
                    }
                }
            </script>
            </body>
            </html>
            """;
    }
}
