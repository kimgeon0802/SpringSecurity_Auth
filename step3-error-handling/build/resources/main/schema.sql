-- ========================================
-- [Step 3] 데이터베이스 스키마 (Step 2와 동일)
-- ========================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    dormant     BOOLEAN      NOT NULL DEFAULT FALSE,
    login_fail_count INT     NOT NULL DEFAULT 0,
    last_login  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authorities (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    authority   VARCHAR(50)  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS login_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    success     BOOLEAN      NOT NULL,
    ip_address  VARCHAR(45),
    message     VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
