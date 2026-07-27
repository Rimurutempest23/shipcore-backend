CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    name VARCHAR(150) NOT NULL,
    ruc VARCHAR(20),
    address VARCHAR(255),
    phone VARCHAR(20),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS carriers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    name VARCHAR(120) NOT NULL,
    service_type VARCHAR(60) NOT NULL,
    contact_email VARCHAR(80),
    phone VARCHAR(20),
    organization_id BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS carrier_rates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    zone VARCHAR(60) NOT NULL,
    min_weight_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    max_weight_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    base_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    price_per_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    valid_from DATE NOT NULL DEFAULT (CURRENT_DATE),
    valid_to DATE NOT NULL DEFAULT (CURRENT_DATE),
    version_number INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    lock_version BIGINT,
    carrier_id BIGINT,
    organization_id BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quote_addresses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    address_type VARCHAR(31) NOT NULL,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    city VARCHAR(80) NOT NULL,
    district VARCHAR(80) NOT NULL,
    zone VARCHAR(60) NOT NULL,
    street VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS quotes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    package_weight_kg DECIMAL(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    organization_id BIGINT,
    created_by_user_id BIGINT,
    origin_address_id BIGINT,
    destination_address_id BIGINT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

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

CALL add_column_if_missing('organizations', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('organizations', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('organizations', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('organizations', 'address', 'VARCHAR(255)');
CALL add_column_if_missing('organizations', 'phone', 'VARCHAR(20)');

CALL add_column_if_missing('users', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('users', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('users', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('users', 'first_name', 'VARCHAR(255)');
CALL add_column_if_missing('users', 'last_name', 'VARCHAR(255)');
CALL add_column_if_missing('users', 'role', 'VARCHAR(255)');
CALL add_column_if_missing('users', 'organization_id', 'BIGINT');

CALL add_column_if_missing('carriers', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('carriers', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('carriers', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('carriers', 'service_type', 'VARCHAR(60) NOT NULL DEFAULT ''STANDARD''');
CALL add_column_if_missing('carriers', 'contact_email', 'VARCHAR(80)');
CALL add_column_if_missing('carriers', 'phone', 'VARCHAR(20)');
CALL add_column_if_missing('carriers', 'organization_id', 'BIGINT');

CALL add_column_if_missing('carrier_rates', 'active', 'BIT NOT NULL DEFAULT 1');
CALL add_column_if_missing('carrier_rates', 'created_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('carrier_rates', 'updated_at', 'DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)');
CALL add_column_if_missing('carrier_rates', 'min_weight_kg', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('carrier_rates', 'max_weight_kg', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('carrier_rates', 'base_price', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('carrier_rates', 'price_per_kg', 'DECIMAL(10, 2) NOT NULL DEFAULT 0');
CALL add_column_if_missing('carrier_rates', 'valid_from', 'DATE NOT NULL DEFAULT (CURRENT_DATE)');
CALL add_column_if_missing('carrier_rates', 'valid_to', 'DATE NOT NULL DEFAULT (CURRENT_DATE)');
CALL add_column_if_missing('carrier_rates', 'version_number', 'INT NOT NULL DEFAULT 1');
CALL add_column_if_missing('carrier_rates', 'status', 'VARCHAR(20) NOT NULL DEFAULT ''ACTIVE''');
CALL add_column_if_missing('carrier_rates', 'lock_version', 'BIGINT');
CALL add_column_if_missing('carrier_rates', 'organization_id', 'BIGINT');

UPDATE users
SET organization_id = (SELECT id FROM organizations ORDER BY id LIMIT 1)
WHERE organization_id IS NULL
  AND EXISTS (SELECT 1 FROM organizations);

UPDATE carriers
SET organization_id = (SELECT id FROM organizations ORDER BY id LIMIT 1)
WHERE organization_id IS NULL
  AND EXISTS (SELECT 1 FROM organizations);

UPDATE carrier_rates r
JOIN carriers c ON c.id = r.carrier_id
SET r.organization_id = c.organization_id
WHERE r.organization_id IS NULL;

DROP PROCEDURE IF EXISTS add_column_if_missing;
