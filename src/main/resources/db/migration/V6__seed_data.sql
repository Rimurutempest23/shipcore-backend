-- Seed initial organization
INSERT INTO organizations (id, active, created_at, updated_at, name, ruc, address, phone, country, plan, soft_limit, hard_limit, current_usage)
VALUES (1, true, NOW(), NOW(), 'ShipCore Demo Org', '20123456789', 'Av. Javier Prado Este 123', '014445555', 'PE', 'starter', 1000, 1200, 0)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed initial users
-- Password hash for 'admin123' and 'operador123': $2a$10$4.q51F8oX/d1O.R6V4/U/eC4a1M9J9K1L9M9N9O9P9Q9R9S9T9U9V
-- Standard BCrypt hash for 'admin123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
INSERT INTO users (id, active, created_at, updated_at, first_name, last_name, email, password, role, organization_id)
VALUES 
(1, true, NOW(), NOW(), 'Admin', 'ShipCore', 'admin@shipcore.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', 1),
(2, true, NOW(), NOW(), 'Operador', 'ShipCore', 'operador@shipcore.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_OPERATOR', 1),
(3, true, NOW(), NOW(), 'Lucía', 'Fernández', 'admin@andina.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', 1)
ON DUPLICATE KEY UPDATE password='$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG';


-- Seed initial carriers
INSERT INTO carriers (id, active, created_at, updated_at, name, code, service_type, logo_url, contact_email, phone, organization_id)
VALUES
(1, true, NOW(), NOW(), 'DHL Express', 'DHL', 'standard', 'https://logo.clearbit.com/dhl.com', 'contacto@dhl.pe', '018001234', 1),
(2, true, NOW(), NOW(), 'FedEx Priority', 'FEDEX', 'standard', 'https://logo.clearbit.com/fedex.com', 'soporte@fedex.pe', '018005678', 1),
(3, true, NOW(), NOW(), 'Olva Courier', 'OLVA', 'standard', 'https://logo.clearbit.com/olvacourier.com', 'consultas@olva.pe', '017130000', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed initial carrier rates
INSERT INTO carrier_rates (id, active, created_at, updated_at, zone, service_type, min_weight_kg, max_weight_kg, base_price, price_per_kg, price_per_km, transit_days_min, transit_days_max, valid_from, valid_to, version_number, status, source, lock_version, carrier_id, organization_id)
VALUES
(1, true, NOW(), NOW(), 'local', 'standard', 0.10, 50.00, 15.00, 2.50, 0.50, 1, 2, '2026-01-01', '2026-12-31', 1, 'ACTIVE', 'MANUAL', 0, 1, 1),
(2, true, NOW(), NOW(), 'nacional', 'standard', 0.10, 50.00, 25.00, 4.00, 1.00, 2, 4, '2026-01-01', '2026-12-31', 1, 'ACTIVE', 'MANUAL', 0, 2, 1),
(3, true, NOW(), NOW(), 'local', 'standard', 0.10, 30.00, 12.00, 2.00, 0.40, 1, 3, '2026-01-01', '2026-12-31', 1, 'ACTIVE', 'MANUAL', 0, 3, 1)
ON DUPLICATE KEY UPDATE zone=VALUES(zone);

-- Seed initial shipping rules
INSERT INTO shipping_rules (id, active, created_at, updated_at, name, field, operator, value, action, action_value, priority, organization_id)
VALUES
(1, true, NOW(), NOW(), 'Sobrepeso', 'WEIGHT', 'GT', '30', 'SURCHARGE', 10.00, 5, 1),
(2, true, NOW(), NOW(), 'Descuento Zona Local', 'ZONE', 'EQ', 'local', 'DISCOUNT', 5.00, 3, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);
