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

CALL add_column_if_missing('organizations', 'country', 'VARCHAR(2) NOT NULL DEFAULT ''PE''');
CALL add_column_if_missing('organizations', 'plan', 'VARCHAR(30) NOT NULL DEFAULT ''starter''');
CALL add_column_if_missing('organizations', 'soft_limit', 'INT NOT NULL DEFAULT 1000');
CALL add_column_if_missing('organizations', 'hard_limit', 'INT NOT NULL DEFAULT 1200');
CALL add_column_if_missing('organizations', 'current_usage', 'INT NOT NULL DEFAULT 0');

CALL add_column_if_missing('carriers', 'code', 'VARCHAR(60)');
CALL add_column_if_missing('carriers', 'logo_url', 'VARCHAR(500)');

CALL add_column_if_missing('carrier_rates', 'service_type', 'VARCHAR(60) NOT NULL DEFAULT ''standard''');
CALL add_column_if_missing('carrier_rates', 'price_per_km', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('carrier_rates', 'transit_days_min', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('carrier_rates', 'transit_days_max', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('carrier_rates', 'source', 'VARCHAR(20) NOT NULL DEFAULT ''MANUAL''');

CALL add_column_if_missing('quotes', 'origin', 'VARCHAR(255)');
CALL add_column_if_missing('quotes', 'destination', 'VARCHAR(255)');
CALL add_column_if_missing('quotes', 'origin_zone', 'VARCHAR(60)');
CALL add_column_if_missing('quotes', 'dest_zone', 'VARCHAR(60)');
CALL add_column_if_missing('quotes', 'distance_km', 'DECIMAL(10, 2)');
CALL add_column_if_missing('quotes', 'length_cm', 'DECIMAL(10, 2)');
CALL add_column_if_missing('quotes', 'width_cm', 'DECIMAL(10, 2)');
CALL add_column_if_missing('quotes', 'height_cm', 'DECIMAL(10, 2)');
CALL add_column_if_missing('quotes', 'service_type', 'VARCHAR(30)');
CALL add_column_if_missing('quotes', 'quote_status', 'VARCHAR(30)');

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    phone VARCHAR(20),
    address VARCHAR(200),
    bio VARCHAR(500),
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shipping_rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    name VARCHAR(120) NOT NULL,
    field VARCHAR(40) NOT NULL,
    operator VARCHAR(40) NOT NULL,
    value VARCHAR(120) NOT NULL,
    action VARCHAR(40) NOT NULL,
    action_value DECIMAL(10, 2) NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_shipping_rules_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quote_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    rate_version_used INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    transit_days_min INT NOT NULL,
    transit_days_max INT NOT NULL,
    restrictions VARCHAR(500) NOT NULL DEFAULT '',
    selected BIT NOT NULL DEFAULT 0,
    preferred BIT NOT NULL DEFAULT 0,
    quote_id BIGINT NOT NULL,
    carrier_id BIGINT NOT NULL,
    carrier_rate_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_quote_results_quote FOREIGN KEY (quote_id) REFERENCES quotes (id),
    CONSTRAINT fk_quote_results_carrier FOREIGN KEY (carrier_id) REFERENCES carriers (id),
    CONSTRAINT fk_quote_results_carrier_rate FOREIGN KEY (carrier_rate_id) REFERENCES carrier_rates (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    environment VARCHAR(20) NOT NULL,
    key_preview VARCHAR(40) NOT NULL,
    key_hash VARCHAR(255) NOT NULL,
    quota_limit INT NOT NULL DEFAULT 1000,
    usage_count INT NOT NULL DEFAULT 0,
    last_used_at DATETIME(6),
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_api_keys_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

DROP PROCEDURE IF EXISTS add_column_if_missing;
