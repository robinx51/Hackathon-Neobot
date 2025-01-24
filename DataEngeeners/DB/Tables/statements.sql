-- Создание таблицы с заявками

CREATE TABLE statements (
    statement_id INTEGER PRIMARY KEY,
    user_id INTEGER REFERENCES users (user_id),
    course_id INTEGER REFERENCES courses (course_id),
    statement_status VARCHAR(15) DEFAULT 'Новая',
    creation_date TIMESTAMP DEFAULT NOW(),
    changed_date TIMESTAMP DEFAULT NOW()
)
