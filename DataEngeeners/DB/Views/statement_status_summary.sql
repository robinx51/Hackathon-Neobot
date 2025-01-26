-- Витрина, отображающая количество заявок по статусу заявки

CREATE OR REPLACE VIEW statement_status_summary AS
SELECT 
    s.statement_status, 
    COUNT(s.statement_id) AS value
FROM 
    statements s
GROUP BY 
    s.statement_status;