-- ========================================
-- [Step 2] 초기 데이터 삽입
-- ========================================

-- 비밀번호는 BCrypt로 인코딩된 값 (모든 계정 공통)
-- 원본 비밀번호: 1234
-- BCrypt hash : $2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim

-- 일반 사용자 (정상)
INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'user@example.com', true, false, 0);

-- 관리자 (정상)
INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('admin', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'admin@example.com', true, false, 0);

-- ★ 휴면 계정 (dormant = true)
INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('dormant_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'dormant@example.com', true, true, 0);

-- ★ 비활성 계정 (enabled = false)
INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('disabled_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'disabled@example.com', false, false, 0);

-- ★ 비밀번호 5회 오류 계정 (login_fail_count = 5)
INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('locked_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'locked@example.com', true, false, 5);

-- 권한 부여
INSERT INTO authorities (user_id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO authorities (user_id, authority) VALUES (2, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (3, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (4, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (5, 'ROLE_USER');
