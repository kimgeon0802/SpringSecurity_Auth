-- ========================================
-- [Step 2] 데이터베이스 스키마 정의
-- ========================================
-- H2 Database 사용 (교육용 인메모리 DB)
-- 애플리케이션 시작 시 자동 실행

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    dormant     BOOLEAN      NOT NULL DEFAULT FALSE,  -- ★ 휴면 계정 여부
    login_fail_count INT     NOT NULL DEFAULT 0,       -- ★ 로그인 실패 횟수
    last_login  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 권한 테이블
CREATE TABLE IF NOT EXISTS authorities (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    authority   VARCHAR(50)  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 로그인 이력 테이블 (감사용)
CREATE TABLE IF NOT EXISTS login_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    success     BOOLEAN      NOT NULL,
    ip_address  VARCHAR(45),
    message     VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
