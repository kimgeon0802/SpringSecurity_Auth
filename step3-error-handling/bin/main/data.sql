-- ========================================
-- [Step 3] 초기 데이터 (Step 2와 동일)
-- ========================================
-- 비밀번호: 모두 '1234' (BCrypt)

INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'user@example.com', true, false, 0);

INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('admin', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'admin@example.com', true, false, 0);

INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('dormant_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'dormant@example.com', true, true, 0);

INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('disabled_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'disabled@example.com', false, false, 0);

INSERT INTO users (username, password, email, enabled, dormant, login_fail_count) VALUES
('locked_user', '$2a$10$C21lK3b1X6y.8XbUaZxcoe9KyJNuNAbp7DfTN9oqoUI9OeR3.Btim', 'locked@example.com', true, false, 5);

INSERT INTO authorities (user_id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT INTO authorities (user_id, authority) VALUES (2, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (3, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (4, 'ROLE_USER');
INSERT INTO authorities (user_id, authority) VALUES (5, 'ROLE_USER');
