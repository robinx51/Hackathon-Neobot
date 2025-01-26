-- Витрина, показывающая количество успешных и неуспешных регистраций

CREATE OR REPLACE VIEW statement_success_or_failed_status_summary AS
SELECT 
	statement_status,
	count(*) AS value
FROM statements 
WHERE statement_status IN ('ACCEPTED', 'REJECTED')
GROUP BY statement_status;