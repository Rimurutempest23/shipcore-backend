ALTER TABLE organizations
    ADD COLUMN last_usage_reset_at DATETIME(6) NULL;

UPDATE organizations
SET last_usage_reset_at = COALESCE(updated_at, created_at, CURRENT_TIMESTAMP(6))
WHERE last_usage_reset_at IS NULL;

UPDATE organizations
SET soft_limit = CASE LOWER(plan)
        WHEN 'enterprise' THEN 10000
        WHEN 'growth' THEN 3000
        ELSE 1000
    END,
    hard_limit = CASE LOWER(plan)
        WHEN 'enterprise' THEN 12000
        WHEN 'growth' THEN 3600
        ELSE 1200
    END;
