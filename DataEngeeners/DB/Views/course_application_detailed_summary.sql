CREATE OR REPLACE VIEW course_application_detailed_summary AS
SELECT 
    c.course_name, 
    s.statement_status, 
    COUNT(s.statement_id) AS value
FROM 
    statements AS s
JOIN 
    courses c ON s.course_id = c.course_id
GROUP BY 
    c.course_name, s.statement_status;