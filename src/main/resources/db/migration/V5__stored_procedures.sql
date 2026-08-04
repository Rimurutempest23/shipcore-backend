DELIMITER //

DROP PROCEDURE IF EXISTS sp_dashboard_summary //

CREATE PROCEDURE sp_dashboard_summary(IN p_organization_id BIGINT)
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM quotes WHERE organization_id = p_organization_id AND active = 1) AS total_quotes,
        (SELECT COUNT(*) FROM quotes WHERE organization_id = p_organization_id AND active = 1 AND MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())) AS monthly_quotes,
        (SELECT COALESCE(SUM(qr.price * 0.15), 0.00) FROM quote_results qr JOIN quotes q ON qr.quote_id = q.id WHERE q.organization_id = p_organization_id AND qr.selected = 1 AND q.active = 1) AS estimated_savings,
        (SELECT current_usage FROM organizations WHERE id = p_organization_id) AS current_usage,
        (SELECT soft_limit FROM organizations WHERE id = p_organization_id) AS soft_limit,
        (SELECT hard_limit FROM organizations WHERE id = p_organization_id) AS hard_limit;
END //

DELIMITER ;
