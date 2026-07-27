CREATE TABLE organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(150) NOT NULL,
    ruc VARCHAR(20),
    address VARCHAR(255),
    phone VARCHAR(20),
    PRIMARY KEY (id),
    CONSTRAINT uk_organizations_ruc UNIQUE (ruc)
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
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

CREATE TABLE carriers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(120) NOT NULL,
    service_type VARCHAR(60) NOT NULL,
    contact_email VARCHAR(80),
    phone VARCHAR(20),
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_carriers_organization_name UNIQUE (organization_id, name),
    CONSTRAINT fk_carriers_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

CREATE TABLE carrier_rates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    zone VARCHAR(60) NOT NULL,
    min_weight_kg DECIMAL(10, 2) NOT NULL,
    max_weight_kg DECIMAL(10, 2) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    price_per_kg DECIMAL(10, 2) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    version_number INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    lock_version BIGINT,
    carrier_id BIGINT NOT NULL,
    organization_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_rate_lookup (organization_id, zone, status, valid_from, valid_to),
    INDEX idx_rate_carrier_zone (carrier_id, zone),
    CONSTRAINT fk_rates_carrier FOREIGN KEY (carrier_id) REFERENCES carriers (id),
    CONSTRAINT fk_rates_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
) ENGINE=InnoDB;

CREATE TABLE quote_addresses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    address_type VARCHAR(31) NOT NULL,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    city VARCHAR(80) NOT NULL,
    district VARCHAR(80) NOT NULL,
    zone VARCHAR(60) NOT NULL,
    street VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE quotes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    package_weight_kg DECIMAL(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
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
