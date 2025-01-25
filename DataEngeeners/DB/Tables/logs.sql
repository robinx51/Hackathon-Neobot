-- Описания таблиц для логов

-- Создание таблички для логов, связанных с загрузкой данных

CREATE TABLE data_logs (
    event VARCHAR(10) NOT NULL,
    time_of_event TIMESTAMP,
    message VARCHAR(255)
)