DROP PROCEDURE IF EXISTS add_column_if_missing;

DELIMITER //
CREATE PROCEDURE add_column_if_missing(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_definition TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = p_table_name
          AND column_name = p_column_name
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE ', p_table_name, ' ADD COLUMN ', p_column_name, ' ', p_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shipping_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quote_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quote_id BIGINT NOT NULL,
    carrier_id BIGINT NOT NULL,
    carrier_rate_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CALL add_column_if_missing('user_profiles', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('user_profiles', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('user_profiles', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('user_profiles', 'phone', 'VARCHAR(20)');
CALL add_column_if_missing('user_profiles', 'address', 'VARCHAR(200)');
CALL add_column_if_missing('user_profiles', 'bio', 'VARCHAR(500)');

CALL add_column_if_missing('shipping_rules', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('shipping_rules', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('shipping_rules', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('shipping_rules', 'name', 'VARCHAR(120) NOT NULL DEFAULT ''''');
CALL add_column_if_missing('shipping_rules', 'field', 'VARCHAR(40) NOT NULL DEFAULT ''WEIGHT''');
CALL add_column_if_missing('shipping_rules', 'operator', 'VARCHAR(40) NOT NULL DEFAULT ''EQ''');
CALL add_column_if_missing('shipping_rules', 'value', 'VARCHAR(120) NOT NULL DEFAULT ''''');
CALL add_column_if_missing('shipping_rules', 'action', 'VARCHAR(40) NOT NULL DEFAULT ''SURCHARGE''');
CALL add_column_if_missing('shipping_rules', 'action_value', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('shipping_rules', 'priority', 'INT NOT NULL DEFAULT 0');

CALL add_column_if_missing('quote_results', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('quote_results', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('quote_results', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('quote_results', 'rate_version_used', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('quote_results', 'price', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('quote_results', 'transit_days_min', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('quote_results', 'transit_days_max', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('quote_results', 'restrictions', 'VARCHAR(500) NOT NULL DEFAULT ''''');
CALL add_column_if_missing('quote_results', 'selected', 'BIT NOT NULL DEFAULT 0');
CALL add_column_if_missing('quote_results', 'preferred', 'BIT NOT NULL DEFAULT 0');

CALL add_column_if_missing('api_keys', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('api_keys', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('api_keys', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('api_keys', 'environment', 'VARCHAR(20) NOT NULL DEFAULT ''SANDBOX''');
CALL add_column_if_missing('api_keys', 'key_preview', 'VARCHAR(40) NOT NULL DEFAULT ''''');
CALL add_column_if_missing('api_keys', 'key_hash', 'VARCHAR(255) NOT NULL DEFAULT ''''');
CALL add_column_if_missing('api_keys', 'quota_limit', 'INT NOT NULL DEFAULT 1000');
CALL add_column_if_missing('api_keys', 'usage_count', 'INT NOT NULL DEFAULT 0');
CALL add_column_if_missing('api_keys', 'last_used_at', 'DATETIME(6)');

DROP PROCEDURE IF EXISTS add_column_if_missing;
