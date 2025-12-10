-- 1. user 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_score INT NOT NULL DEFAULT 0
);

-- 2. news 테이블
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    url TEXT NOT NULL,
    published_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. summary 테이블
CREATE TABLE summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. summary_news (N:N 매핑 테이블)
CREATE TABLE summary_news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,

    CONSTRAINT fk_summarynews_summary
        FOREIGN KEY (summary_id)
        REFERENCES summary (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_summarynews_news
        FOREIGN KEY (news_id)
        REFERENCES news (id)
        ON DELETE CASCADE
);

-- 5. quiz (summary와 1:1)
CREATE TABLE quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    summary_id BIGINT NOT NULL UNIQUE,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quiz_summary
        FOREIGN KEY (summary_id)
        REFERENCES summary (id)
        ON DELETE CASCADE
);

-- 6. quiz_answers (사용자 답변 기록)
CREATE TABLE quiz_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    user_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_answer_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_answer_quiz
        FOREIGN KEY (quiz_id)
        REFERENCES quiz (id)
        ON DELETE CASCADE
);

-- 7. user_read_logs (사용자가 어떤 뉴스 읽었는지)
CREATE TABLE user_read_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    read_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_log_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_log_news
        FOREIGN KEY (news_id)
        REFERENCES news (id)
        ON DELETE CASCADE
);

-- 8. user_quiz (사용자 점수 / 정답 여부 / 선택 기록)
CREATE TABLE user_quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    quiz_score INT NOT NULL,
    user_answer TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_userquiz_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_userquiz_quiz
        FOREIGN KEY (quiz_id)
        REFERENCES quiz (id)
        ON DELETE CASCADE
);
