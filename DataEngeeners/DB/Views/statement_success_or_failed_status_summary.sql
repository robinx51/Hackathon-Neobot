CREATE OR REPLACE VIEW statement_success_or_failed_status_summary AS
SELECT 
	statement_status,
	count(*) AS value
FROM statements 
WHERE statement_status IN ('Принято', 'Отклонено')
GROUP BY statement_status;