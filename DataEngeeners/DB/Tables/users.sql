-- Создание таблицы пользователя

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY, 
    telegram_id VARCHAR(10) NOT NULL,
    role VARCHAR(10) DEFAULT 'visitor',
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(40) NOT NULL,
    city VARCHAR(40) NOT NULL,
    phone_number VARCHAR(11) NOT NULL
)