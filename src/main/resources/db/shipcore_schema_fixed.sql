CREATE DATABASE IF NOT EXISTS shipcore_db;
USE shipcore_db;

CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    name VARCHAR(150) NOT NULL,
    ruc VARCHAR(20),
    address VARCHAR(255),
    phone VARCHAR(20),
    country VARCHAR(2) NOT NULL DEFAULT 'PE',
    plan VARCHAR(30) NOT NULL DEFAULT 'starter',
    soft_limit INT NOT NULL DEFAULT 1000,
    hard_limit INT NOT NULL DEFAULT 1200,
    current_usage INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_organizations_ruc UNIQUE (ruc)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

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

CREATE TABLE IF NOT EXISTS carriers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    name VARCHAR(120) NOT NULL,
    code VARCHAR(60),
    service_type VARCHAR(60) NOT NULL,
    logo_url VARCHAR(500),
    contact_email VARCHAR(80),
    phone VARCHAR(20),
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_carriers_organization_name UNIQUE (organization_id, name),
    CONSTRAINT fk_carriers_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS carrier_rates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    zone VARCHAR(60) NOT NULL,
    service_type VARCHAR(60) NOT NULL DEFAULT 'standard',
    min_weight_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    max_weight_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    base_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    price_per_kg DECIMAL(10, 2) NOT NULL DEFAULT 0,
    price_per_km DECIMAL(10, 2) NOT NULL DEFAULT 0,
    transit_days_min INT NOT NULL DEFAULT 1,
    transit_days_max INT NOT NULL DEFAULT 1,
    valid_from DATE NOT NULL DEFAULT (CURRENT_DATE),
    valid_to DATE NOT NULL DEFAULT (CURRENT_DATE),
    version_number INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    source VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    lock_version BIGINT,
    carrier_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_rate_lookup (organization_id, zone, status, valid_from, valid_to),
    INDEX idx_rate_carrier_zone (carrier_id, zone),
    CONSTRAINT fk_rates_carrier FOREIGN KEY (carrier_id) REFERENCES carriers (id),
    CONSTRAINT fk_rates_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
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
    origin VARCHAR(255),
    destination VARCHAR(255),
    origin_zone VARCHAR(60),
    dest_zone VARCHAR(60),
    distance_km DECIMAL(10, 2),
    length_cm DECIMAL(10, 2),
    width_cm DECIMAL(10, 2),
    height_cm DECIMAL(10, 2),
    service_type VARCHAR(30),
    status VARCHAR(30) NOT NULL,
    quote_status VARCHAR(30),
    organization_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    origin_address_id BIGINT NOT NULL,
    destination_address_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_quotes_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_quotes_user FOREIGN KEY (created_by_user_id) REFERENCES users (id),
    CONSTRAINT fk_quotes_origin_address FOREIGN KEY (origin_address_id) REFERENCES quote_addresses (id),
    CONSTRAINT fk_quotes_destination_address FOREIGN KEY (destination_address_id) REFERENCES quote_addresses (id)
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

INSERT INTO organizations (
    id, active, created_at, updated_at, name, ruc, address, phone, country, plan, soft_limit, hard_limit, current_usage
) VALUES (
    1, 1, NOW(6), NOW(6), 'ShipCore Demo', '20123456789', 'Lima, Peru', '999999999', 'PE', 'starter', 1000, 1200, 0
) ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    name = VALUES(name),
    address = VALUES(address),
    phone = VALUES(phone);

INSERT INTO users (
    id, active, created_at, updated_at, first_name, last_name, email, password, role, organization_id
) VALUES (
    1,
    1,
    NOW(6),
    NOW(6),
    'Admin',
    'Demo',
    'admin@shipcore.com',
    '$2a$10$cTN7DGdxBNHT6tMrqr4/Xu9Xb2MNPVsu19BECzjxMZAGV30yM.3Om',
    'ROLE_ADMIN',
    1
) ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    password = VALUES(password),
    role = VALUES(role),
    organization_id = VALUES(organization_id);

INSERT INTO carriers (
    id, active, created_at, updated_at, name, code, service_type, logo_url, contact_email, phone, organization_id
) VALUES (
    1, 1, NOW(6), NOW(6), 'ShipCore Express', 'SCE', 'standard', NULL, 'ops@shipcore.com', '999999999', 1
) ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    code = VALUES(code),
    service_type = VALUES(service_type),
    contact_email = VALUES(contact_email),
    phone = VALUES(phone);

INSERT INTO carrier_rates (
    id, active, created_at, updated_at, zone, service_type, min_weight_kg, max_weight_kg,
    base_price, price_per_kg, price_per_km, transit_days_min, transit_days_max,
    valid_from, valid_to, version_number, status, source, lock_version, carrier_id, organization_id
) VALUES (
    1, 1, NOW(6), NOW(6), 'local', 'standard', 0, 30, 10, 2, 0.50, 1, 2,
    CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR), 1, 'ACTIVE', 'MANUAL', 0, 1, 1
) ON DUPLICATE KEY UPDATE
    updated_at = VALUES(updated_at),
    base_price = VALUES(base_price),
    price_per_kg = VALUES(price_per_kg),
    price_per_km = VALUES(price_per_km),
    status = VALUES(status);
