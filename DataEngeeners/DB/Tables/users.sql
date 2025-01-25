-- Создание таблицы пользователя

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY, 
    telegram_id BIGINT NOT NULL,
    role VARCHAR(10) DEFAULT 'visitor',
    first_name VARCHAR(50) NULL,
    last_name VARCHAR(50) NULL,
    email VARCHAR(40) NULL,
    city VARCHAR(40) NULL,
    phone_number CHAR(12) NULL
)