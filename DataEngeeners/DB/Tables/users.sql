-- Создание таблицы пользователя

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY, 
    telegram_id BIGINT NOT NULL,
    role VARCHAR(20) DEFAULT 'EXTERNAL_USER',
    first_name VARCHAR(30) NULL,
    last_name VARCHAR(30) NULL,
    email VARCHAR(64) NULL,
    city VARCHAR(35) NULL,
    phone_number CHAR(12) NULL
)