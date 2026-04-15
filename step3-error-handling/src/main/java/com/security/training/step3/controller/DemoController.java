package com.security.training.step3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping({"/", "/demo"})
    public String demo() {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>Step 3 - Error Handling 실습</title>
                <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; }
                    .container { max-width: 980px; margin: 40px auto; padding: 0 20px; }
                    h1 { font-size: 1.4rem; color: #333; margin-bottom: 6px; }
                    .subtitle { color: #888; font-size: 0.9rem; margin-bottom: 24px; }
                    .card { background: white; border-radius: 8px; padding: 24px; margin-bottom: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
                    .card h2 { font-size: 1rem; color: #444; margin-bottom: 16px; border-bottom: 2px solid #e74c3c; padding-bottom: 8px; }
                    .step-label { display: inline-block; background: #e74c3c; color: white; border-radius: 12px; padding: 2px 10px; font-size: 0.75rem; margin-bottom: 8px; }
                    .step-label.green { background: #2ecc71; }
                    .step-label.blue  { background: #4f8ef7; }
                    input { padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem; margin-right: 8px; width: 160px; }
                    .btn-row { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
                    button { padding: 8px 14px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.82rem; color: white; }
                    .btn-primary { background: #4f8ef7; }
                    .btn-success { background: #2ecc71; }
                    .btn-danger  { background: #e74c3c; }
                    .btn-warning { background: #e67e22; }
                    .btn-dark    { background: #6c757d; }
                    button:hover { opacity: 0.85; }
                    .result { margin-top: 14px; padding: 12px; background: #f8f9fa; border-radius: 4px; font-size: 0.82rem; font-family: monospace; white-space: pre-wrap; word-break: break-all; min-height: 48px; border-left: 3px solid #ddd; }
                    .result.success { border-left-color: #2ecc71; background: #f0fff4; }
                    .result.error   { border-left-color: #e74c3c; background: #fff5f5; }
                    .result.warn    { border-left-color: #e67e22; background: #fff9f0; }
                    .compare-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
                    .compare-box { padding: 14px; border-radius: 6px; font-size: 0.82rem; font-family: monospace; white-space: pre-wrap; }
                    .compare-box.bad  { background: #fff5f5; border: 1px solid #f5c6cb; }
                    .compare-box.good { background: #f0fff4; border: 1px solid #c3e6cb; }
                    .compare-label { font-weight: bold; margin-bottom: 8px; font-family: sans-serif; font-size: 0.85rem; }
                    .code-badge { display: inline-block; background: #e74c3c; color: white; padding: 2px 8px; border-radius: 4px; font-size: 0.78rem; font-family: monospace; }
                    .code-badge.green { background: #27ae60; }
                    .token-box { padding: 10px; background: #fff8e1; border: 1px solid #ffe082; border-radius: 4px; font-size: 0.78rem; font-family: monospace; word-break: break-all; margin-top: 10px; display: none; }
                    table { width: 100%; border-collapse: collapse; font-size: 0.84rem; }
                    th { background: #e74c3c; color: white; padding: 8px 12px; text-align: left; }
                    td { padding: 8px 12px; border-bottom: 1px solid #eee; }
                </style>
            </head>
            <body>
            <div class="container">
                <h1>🚨 Step 3 — Error Handling 실습</h1>
                <p class="subtitle">인증/인가 실패 시 JSON 에러 응답 규격화 (AuthenticationEntryPoint / AccessDeniedHandler)</p>

                <!-- Step 2 vs Step 3 비교 -->
                <div class="card">
                    <span class="step-label">핵심 비교</span>
                    <h2>Step 2 vs Step 3 — 에러 응답 차이</h2>
                    <div class="compare-grid">
                        <div>
                            <div class="compare-label" style="color:#e74c3c;">❌ Step 2 (8082) — 토큰 없이 접근</div>
                            <div class="compare-box bad">HTTP/1.1 403
Content-Length: 0

(본문 없음)

→ 프론트엔드가 왜 막혔는지 알 수 없음</div>
                        </div>
                        <div>
                            <div class="compare-label" style="color:#27ae60;">✅ Step 3 (8083) — 토큰 없이 접근</div>
                            <div class="compare-box good">HTTP/1.1 401
Content-Type: application/json

{
  "status": 401,
  "error": "Unauthorized",
  "code": "AUTH_005",
  "message": "인증이 필요합니다.",
  "path": "/api/me",
  "timestamp": "2026-04-13T15:00:00"
}</div>
                        </div>
                    </div>
                </div>

                <!-- 에러 코드 규격 -->
                <div class="card">
                    <span class="step-label blue">에러 코드</span>
                    <h2>ErrorResponse 규격 — 프론트엔드 분기 처리용</h2>
                    <table>
                        <tr><th>code</th><th>HTTP</th><th>상황</th><th>프론트 처리 예시</th></tr>
                        <tr><td><span class="code-badge">AUTH_001</span></td><td>401</td><td>비밀번호 오류</td><td>"아이디/비밀번호를 확인하세요"</td></tr>
                        <tr><td><span class="code-badge">AUTH_002</span></td><td>401</td><td>휴면/비활성 계정</td><td>"휴면 해제 안내 페이지로 이동"</td></tr>
                        <tr><td><span class="code-badge">AUTH_003</span></td><td>401</td><td>계정 잠금</td><td>"관리자에게 문의하세요" 팝업</td></tr>
                        <tr><td><span class="code-badge">AUTH_005</span></td><td>401</td><td>토큰 없음/만료</td><td>로그인 페이지로 자동 이동</td></tr>
                        <tr><td><span class="code-badge" style="background:#e67e22">AUTHZ_001</span></td><td>403</td><td>권한 부족</td><td>"접근 권한이 없습니다" 팝업</td></tr>
                    </table>
                </div>

                <!-- STEP 1: 로그인 -->
                <div class="card">
                    <span class="step-label blue">STEP 1</span>
                    <h2>로그인 → JWT 발급</h2>
                    <input id="username" value="user" placeholder="아이디" />
                    <input id="password" type="password" value="1234" placeholder="비밀번호" />
                    <div class="btn-row">
                        <button class="btn-success" onclick="doLogin('user','1234')">✅ user / 1234</button>
                        <button class="btn-success" onclick="doLogin('admin','1234')">✅ admin / 1234 (ADMIN)</button>
                        <button class="btn-warning" onclick="doLogin('dormant_user','1234')">😴 휴면 계정</button>
                        <button class="btn-dark"    onclick="doLogin('disabled_user','1234')">🚫 비활성 계정</button>
                        <button class="btn-danger"  onclick="doLogin('locked_user','1234')">🔒 잠금 계정</button>
                        <button class="btn-danger"  onclick="doLogin('user','wrong')">❌ 비밀번호 오류</button>
                    </div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px; margin-top:14px;">
                        <div>
                            <div style="font-size:0.8rem; color:#888; margin-bottom:4px; font-weight:600;">📤 REQUEST</div>
                            <pre id="loginReqPanel" style="display:none; padding:12px; background:#f0f4ff; border-left:3px solid #4f8ef7; border-radius:4px; font-size:0.78rem; white-space:pre-wrap; word-break:break-all;"></pre>
                            <div id="loginReqEmpty" style="padding:12px; background:#f0f4ff; border-left:3px solid #4f8ef7; border-radius:4px; font-size:0.82rem; color:#aaa; min-height:48px;">계정 버튼 클릭 시 요청 정보가 표시됩니다.</div>
                        </div>
                        <div>
                            <div style="font-size:0.8rem; color:#888; margin-bottom:4px; font-weight:600;">📥 RESPONSE</div>
                            <div id="loginResult" class="result" style="min-height:48px;">계정 버튼을 클릭하면 JSON 에러 응답을 확인합니다.</div>
                        </div>
                    </div>
                    <div id="tokenBox" class="token-box"></div>
                </div>

                <!-- STEP 2: 에러 코드 시나리오 -->
                <div class="card">
                    <span class="step-label">STEP 2</span>
                    <h2>에러 코드별 시나리오 테스트</h2>
                    <div class="btn-row">
                        <button class="btn-danger"  onclick="callNoToken()">🚫 토큰 없이 접근 → AUTH_005</button>
                        <button class="btn-danger"  onclick="callBadToken()">🚫 잘못된 토큰 → AUTH_005</button>
                        <button class="btn-warning" onclick="callUserOnAdmin()">⚠️ user로 ADMIN API → AUTHZ_001</button>
                        <button class="btn-success" onclick="callWithToken('/api/me')">✅ 내 정보 조회 (인증 필요)</button>
                        <button class="btn-success" onclick="callAdminWithAdmin()">✅ admin으로 ADMIN API</button>
                    </div>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px; margin-top:14px;">
                        <div>
                            <div style="font-size:0.8rem; color:#888; margin-bottom:4px; font-weight:600;">📤 REQUEST</div>
                            <div id="reqPanel" class="result" style="min-height:140px; border-left-color:#4f8ef7; background:#f0f4ff;">버튼 클릭 시 요청 정보가 표시됩니다.</div>
                        </div>
                        <div>
                            <div style="font-size:0.8rem; color:#888; margin-bottom:4px; font-weight:600;">📥 RESPONSE</div>
                            <div id="apiResult" class="result" style="min-height:140px;">버튼 클릭 시 응답 JSON이 표시됩니다.</div>
                        </div>
                    </div>
                    <div style="margin-top:10px; padding:10px 14px; background:#fffbe6; border:1px solid #ffe082; border-radius:4px; font-size:0.8rem; color:#555;">
                        💡 <strong>curl 동일 요청:</strong> <span id="curlHint" style="font-family:monospace;">버튼 클릭 시 curl 명령이 표시됩니다.</span>
                    </div>
                </div>

                <!-- STEP 3: 에러 코드로 프론트 분기 시뮬레이션 -->
                <div class="card">
                    <span class="step-label green">STEP 3</span>
                    <h2>에러 코드 기반 프론트엔드 분기 시뮬레이션</h2>
                    <p style="font-size:0.88rem; color:#666; margin-bottom:14px;">
                        실제 React/Vue 앱에서는 아래처럼 에러 코드에 따라 다른 처리를 합니다.
                    </p>
                    <div class="btn-row">
                        <button class="btn-primary" onclick="simulateLogin('dormant_user','1234')">😴 휴면 계정 로그인 시도</button>
                        <button class="btn-primary" onclick="simulateLogin('locked_user','1234')">🔒 잠금 계정 로그인 시도</button>
                        <button class="btn-primary" onclick="simulateNoAuth()">🚫 미인증 API 호출</button>
                    </div>
                    <div id="simulResult" class="result">버튼 클릭 시 에러 코드에 따른 프론트 처리 시뮬레이션이 표시됩니다.</div>
                </div>
            </div>

            <script>
                const API = 'http://localhost:8083';
                let adminToken = null;

                async function doLogin(username, password) {
                    document.getElementById('username').value = username;
                    document.getElementById('password').value = password;
                    const resultEl  = document.getElementById('loginResult');
                    const tokenEl   = document.getElementById('tokenBox');
                    const reqEl     = document.getElementById('loginReqPanel');
                    resultEl.className = 'result';
                    resultEl.textContent = '요청 중...';

                    const body = { username, password };

                    // 요청 패널 표시
                    document.getElementById('loginReqEmpty').style.display = 'none';
                    reqEl.style.display = 'block';
                    reqEl.textContent =
                        `POST /api/login HTTP/1.1\\n` +
                        `Host: localhost:8083\\n` +
                        `Content-Type: application/json\\n\\n` +
                        `${JSON.stringify(body, null, 2)}\\n\\n` +
                        `── curl ──\\n` +
                        `curl -s -X POST http://localhost:8083/api/login \\\\\\n` +
                        `  -H "Content-Type: application/json" \\\\\\n` +
                        `  -d '{"username":"${username}","password":"${password}"}'`;

                    try {
                        const res = await fetch(`${API}/api/login`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(body)
                        });
                        const data = await res.json();

                        if (res.ok) {
                            localStorage.setItem('jwt_token', data.accessToken);
                            if (username === 'admin') adminToken = data.accessToken;
                            resultEl.className = 'result success';
                            resultEl.textContent =
                                `✅ HTTP 200 OK\\n` +
                                `Content-Type: application/json\\n\\n` +
                                JSON.stringify(data, null, 2) +
                                `\\n\\n→ localStorage['jwt_token'] 저장 완료`;
                            tokenEl.style.display = 'block';
                            tokenEl.textContent = `🔑 ${data.accessToken}`;
                        } else {
                            localStorage.removeItem('jwt_token');
                            resultEl.className = 'result error';
                            resultEl.textContent =
                                `❌ HTTP ${res.status}\\n` +
                                `Content-Type: application/json\\n\\n` +
                                JSON.stringify(data, null, 2);
                            tokenEl.style.display = 'none';
                        }
                    } catch (e) {
                        resultEl.className = 'result error';
                        resultEl.textContent = `❌ 네트워크 오류: ${e.message}`;
                    }
                }

                function showReq(method, path, headers, body) {
                    const el = document.getElementById('reqPanel');
                    el.className = 'result';
                    el.style.borderLeftColor = '#4f8ef7';
                    el.style.background = '#f0f4ff';

                    const headerLines = Object.entries(headers)
                        .map(([k,v]) => `  ${k}: ${v}`)
                        .join('\\n');

                    let text = `${method} ${path} HTTP/1.1\\nHost: localhost:8083\\n`;
                    if (headerLines) text += `${headerLines}\\n`;
                    if (body) text += `\\nBody (JSON):\\n${JSON.stringify(body, null, 2)}`;
                    el.textContent = text;

                    // curl 힌트
                    const curlEl = document.getElementById('curlHint');
                    let curl = `curl -s -X ${method} http://localhost:8083${path}`;
                    Object.entries(headers).forEach(([k,v]) => { curl += ` -H "${k}: ${v}"`; });
                    if (body) curl += ` -d '${JSON.stringify(body)}'`;
                    curlEl.textContent = curl;
                }

                function showRes(resStatus, resOk, label, data) {
                    const el = document.getElementById('apiResult');
                    el.className = resOk ? 'result success' : 'result error';
                    el.textContent =
                        `${resOk ? '✅' : '❌'} HTTP ${resStatus}  ← ${label}\\n` +
                        `Content-Type: application/json\\n\\n` +
                        JSON.stringify(data, null, 2);
                }

                async function callNoToken() {
                    const path = '/api/me';
                    showReq('GET', path, {}, null);
                    try {
                        const res = await fetch(`${API}${path}`);
                        const data = await res.json();
                        showRes(res.status, res.ok, '토큰 없이 접근', data);
                    } catch(e) {
                        document.getElementById('apiResult').textContent = `❌ 오류: ${e.message}`;
                    }
                }

                async function callBadToken() {
                    const path = '/api/me';
                    const badToken = 'INVALID_TOKEN_VALUE';
                    showReq('GET', path, { 'Authorization': `Bearer ${badToken}` }, null);
                    try {
                        const res = await fetch(`${API}${path}`, {
                            headers: { 'Authorization': `Bearer ${badToken}` }
                        });
                        const data = await res.json();
                        showRes(res.status, res.ok, '잘못된 토큰', data);
                    } catch(e) {
                        document.getElementById('apiResult').textContent = `❌ 오류: ${e.message}`;
                    }
                }

                async function callWithToken(path) {
                    const token = localStorage.getItem('jwt_token');
                    const reqEl = document.getElementById('reqPanel');
                    if (!token) {
                        reqEl.className = 'result warn';
                        reqEl.style.background = '';
                        reqEl.textContent = '⚠️ 먼저 로그인하세요.';
                        document.getElementById('curlHint').textContent = '로그인 후 토큰을 먼저 발급받으세요.';
                        return;
                    }
                    const shortToken = token.substring(0,30) + '...';
                    showReq('GET', path, { 'Authorization': `Bearer ${shortToken}` }, null);
                    // curl 은 실제 토큰으로 덮어쓰기
                    document.getElementById('curlHint').textContent =
                        `curl -s http://localhost:8083${path} -H "Authorization: Bearer ${token.substring(0,20)}..."`;
                    try {
                        const res = await fetch(`${API}${path}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const data = await res.json();
                        showRes(res.status, res.ok, '유효한 토큰으로 접근', data);
                    } catch(e) {
                        document.getElementById('apiResult').textContent = `❌ 오류: ${e.message}`;
                    }
                }

                async function callUserOnAdmin() {
                    const path = '/api/admin/users';
                    const token = localStorage.getItem('jwt_token');
                    if (!token) {
                        document.getElementById('reqPanel').textContent = '⚠️ 먼저 user / 1234 로 로그인하세요.';
                        return;
                    }
                    const shortToken = token.substring(0,30) + '...';
                    showReq('GET', path, {
                        'Authorization': `Bearer ${shortToken}`
                    }, null);
                    try {
                        const res = await fetch(`${API}${path}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        const data = await res.json();
                        showRes(res.status, res.ok, 'ROLE_USER → ADMIN 전용 API', data);
                    } catch(e) {
                        document.getElementById('apiResult').textContent = `❌ 오류: ${e.message}`;
                    }
                }

                async function callAdminWithAdmin() {
                    const path = '/api/admin/users';
                    const resultEl = document.getElementById('apiResult');
                    if (!adminToken) {
                        document.getElementById('reqPanel').textContent = '⚠️ 먼저 admin / 1234 로 로그인하세요.';
                        resultEl.className = 'result warn';
                        resultEl.textContent = '⚠️ 먼저 admin / 1234 로 로그인하세요.';
                        return;
                    }
                    const shortToken = adminToken.substring(0,30) + '...';
                    showReq('GET', path, {
                        'Authorization': `Bearer ${shortToken}`
                    }, null);
                    try {
                        const res = await fetch(`${API}${path}`, {
                            headers: { 'Authorization': `Bearer ${adminToken}` }
                        });
                        const data = await res.json();
                        showRes(res.status, res.ok, 'ROLE_ADMIN → ADMIN 전용 API', data);
                    } catch(e) {
                        document.getElementById('apiResult').textContent = `❌ 오류: ${e.message}`;
                    }
                }

                // 프론트엔드 분기 시뮬레이션
                async function simulateLogin(username, password) {
                    const resultEl = document.getElementById('simulResult');
                    const res = await fetch(`${API}/api/login`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ username, password })
                    });
                    const data = await res.json();

                    if (res.ok) {
                        resultEl.className = 'result success';
                        resultEl.textContent = '✅ 로그인 성공 → 메인 페이지로 이동';
                        return;
                    }

                    // ★ 에러 코드별 분기 처리 (실제 React/Vue 코드와 동일한 패턴)
                    let frontendAction = '';
                    switch(data.code) {
                        case 'AUTH_001': frontendAction = '→ "아이디 또는 비밀번호를 확인하세요" 메시지 표시'; break;
                        case 'AUTH_002': frontendAction = '→ "휴면 계정입니다. 휴면 해제 페이지(/dormant-guide)로 이동"'; break;
                        case 'AUTH_003': frontendAction = '→ "계정이 잠겼습니다. 고객센터에 문의하세요" 모달 팝업'; break;
                        case 'AUTH_005': frontendAction = '→ 로그인 페이지(/login)로 자동 리다이렉트'; break;
                        default:         frontendAction = '→ "알 수 없는 오류가 발생했습니다" 표시';
                    }

                    resultEl.className = 'result error';
                    resultEl.textContent =
                        `❌ ${res.status} / code: ${data.code}\\n` +
                        `message: ${data.message}\\n\\n` +
                        `[프론트엔드 처리]\\n${frontendAction}`;
                }

                async function simulateNoAuth() {
                    const resultEl = document.getElementById('simulResult');
                    const res = await fetch(`${API}/api/me`);
                    const data = await res.json();

                    resultEl.className = 'result error';
                    resultEl.textContent =
                        `❌ ${res.status} / code: ${data.code}\\n` +
                        `message: ${data.message}\\n\\n` +
                        `[프론트엔드 처리]\\n→ "세션이 만료되었습니다" 토스트 메시지 + 로그인 페이지 이동`;
                }
            </script>
            </body>
            </html>
            """;
    }
}
