-- ============================================================
-- V8__seed_mock_data.sql
-- Datos del mock local del frontend convertidos a BD real
-- Organización 1 (ShipCore Demo Org / Logística Andina SA)
-- Organización 2 (Cargo Express MX)
-- ============================================================

-- ─── ORGANIZATIONS ───────────────────────────────────────────

INSERT INTO organizations (id, active, created_at, updated_at, name, ruc, address, phone, country, plan, soft_limit, hard_limit, current_usage)
VALUES
  (2, true, NOW(), NOW(), 'Cargo Express MX', '20987654321', 'Av. Insurgentes Sur 1234', '5512345678', 'MX', 'starter', 300, 800, 0)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ─── USERS ───────────────────────────────────────────────────
-- Contraseña para todos: admin123 (BCrypt)
-- $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG

INSERT INTO users (id, active, created_at, updated_at, first_name, last_name, email, password, role, organization_id)
VALUES
  -- Org 1 (Logística Andina SA)
  (4,  true, NOW(), NOW(), 'Lucía',    'Fernández',  'admin@andina.com',    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN',    1),
  (5,  true, NOW(), NOW(), 'Martín',   'Gómez',      'operador@andina.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_OPERATOR', 1),
  -- Org 2 (Cargo Express MX)
  (6,  true, NOW(), NOW(), 'Diego',    'Hernández',  'admin@cargo.mx',      '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN',    2),
  (7,  true, NOW(), NOW(), 'Paula',    'Rivera',     'operador@cargo.mx',   '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_OPERATOR', 2)
ON DUPLICATE KEY UPDATE password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG';

-- Asegurar contraseñas de usuarios YA existentes (id 1-3)
UPDATE users SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE id IN (1, 2, 3);

-- ─── CARRIERS (Org 1 - Logística Andina SA) ──────────────────

INSERT INTO carriers (id, active, created_at, updated_at, name, code, service_type, logo_url, contact_email, phone, organization_id)
VALUES
  (4,  true, NOW(), NOW(), 'Andreani',       'AND', 'standard', NULL, 'contacto@andreani.com',  '08008880258', 1),
  (5,  true, NOW(), NOW(), 'DHL Argentina',  'DHL', 'standard', 'https://logo.clearbit.com/dhl.com', 'dhl@ar.dhl.com', '011-5199-9700', 1),
  (6,  false,NOW(), NOW(), 'Starken',        'STK', 'standard', NULL, 'contacto@starken.cl',   '600-570-0000', 1),
  (7,  true, NOW(), NOW(), 'TCC',            'TCC', 'express',  NULL, 'servicio@tcc.com.co',   '018000183030', 1),
  (8,  true, NOW(), NOW(), 'BlueExpress',    'BLX', 'express',  NULL, 'info@blueexpress.com',  '600-600-2583', 1),
  -- Org 2 (Cargo Express MX)
  (9,  true, NOW(), NOW(), 'DHL México',     'DHM', 'standard', 'https://logo.clearbit.com/dhl.com', 'dhl@mx.dhl.com', '800-765-3345', 2),
  (10, true, NOW(), NOW(), 'FedEx México',   'FDX', 'express',  'https://logo.clearbit.com/fedex.com', 'fedex@mx.fedex.com', '800-377-3339', 2),
  (11, true, NOW(), NOW(), 'Estafeta',       'EST', 'standard', NULL, 'servicio@estafeta.com', '800-903-9090', 2),
  (12, false,NOW(), NOW(), 'Coordinadora',   'COO', 'standard', NULL, 'info@coordinadora.com', '018000-COORDI', 2),
  (13, true, NOW(), NOW(), 'Paquetexpress',  'PQT', 'express',  NULL, 'info@paquetexpress.com','800-011-7722', 2)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ─── CARRIER RATES (Org 1 – Logística Andina SA) ─────────────

INSERT INTO carrier_rates (id, active, created_at, updated_at, zone, service_type, min_weight_kg, max_weight_kg, base_price, price_per_kg, price_per_km, transit_days_min, transit_days_max, valid_from, valid_to, version_number, status, source, lock_version, carrier_id, organization_id)
VALUES
  -- Andreani – local v1
  (4,  true, NOW(), NOW(), 'LOCAL',         'standard', 0.10, 30.00,  850.00,  45.00,  3.50, 1, 2, '2025-08-01', '2026-12-31', 1, 'ACTIVE',   'MANUAL', 0, 4, 1),
  -- DHL Argentina – nacional v1/v2/v3
  (5,  true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1200.00,  65.00,  5.00, 2, 4, '2025-05-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 5, 1),
  (6,  true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1248.00,  66.95,  5.00, 2, 4, '2025-08-01', '2026-12-31', 2, 'INACTIVE', 'MANUAL', 0, 5, 1),
  (7,  true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1297.92,  68.96,  5.00, 2, 4, '2025-11-01', '2026-12-31', 3, 'ACTIVE',   'MANUAL', 0, 5, 1),
  -- Starken – internacional v1/v2
  (8,  true, NOW(), NOW(), 'INTERNACIONAL', 'standard', 0.10, 40.00, 3200.00, 120.00, 10.00, 5, 9, '2025-06-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 6, 1),
  (9,  true, NOW(), NOW(), 'INTERNACIONAL', 'standard', 0.10, 40.00, 3328.00, 123.60, 10.00, 5, 9, '2025-09-01', '2026-12-31', 2, 'ACTIVE',   'IMPORT', 0, 6, 1),
  -- TCC – nacional v1
  (10, true, NOW(), NOW(), 'NACIONAL',      'express',  0.10, 20.00, 1600.00,  85.00,  6.50, 1, 3, '2025-10-01', '2027-03-31', 1, 'ACTIVE',   'IMPORT', 0, 7, 1),
  -- BlueExpress – local v1/v2
  (11, true, NOW(), NOW(), 'LOCAL',         'express',  0.10, 50.00,  780.00,  40.00,  3.00, 1, 2, '2025-05-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 8, 1),
  (12, true, NOW(), NOW(), 'LOCAL',         'express',  0.10, 50.00,  811.20,  41.20,  3.00, 1, 2, '2025-08-01', '2026-12-31', 2, 'ACTIVE',   'MANUAL', 0, 8, 1),
  -- Andreani – nacional v1
  (13, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 30.00, 1050.00,  55.00,  4.50, 2, 5, '2025-09-15', '2027-03-31', 1, 'ACTIVE',   'MANUAL', 0, 4, 1),
  -- DHL Argentina – local v1
  (14, true, NOW(), NOW(), 'LOCAL',         'standard', 0.10, 40.00,  920.00,  48.00,  3.80, 1, 2, '2025-11-10', '2026-12-08', 1, 'ACTIVE',   'MANUAL', 0, 5, 1),
  -- Starken – nacional v1/v2
  (15, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1100.00,  60.00,  5.20, 2, 4, '2025-03-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 6, 1),
  (16, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1144.00,  61.80,  5.20, 2, 4, '2025-06-01', '2026-12-31', 2, 'ACTIVE',   'API',    0, 6, 1),
  -- TCC – internacional v1
  (17, true, NOW(), NOW(), 'INTERNACIONAL', 'express',  0.10, 20.00, 4500.00, 150.00, 12.00, 4, 7, '2025-11-20', '2026-11-20', 1, 'ACTIVE',   'IMPORT', 0, 7, 1),
  -- BlueExpress – internacional (draft)
  (18, true, NOW(), NOW(), 'INTERNACIONAL', 'express',  0.10, 30.00, 3900.00, 130.00, 11.00, 5, 9, '2025-12-25', '2027-06-25', 1, 'DRAFT',    'MANUAL', 0, 8, 1)
ON DUPLICATE KEY UPDATE zone = VALUES(zone);

-- ─── CARRIER RATES (Org 2 – Cargo Express MX) ────────────────

INSERT INTO carrier_rates (id, active, created_at, updated_at, zone, service_type, min_weight_kg, max_weight_kg, base_price, price_per_kg, price_per_km, transit_days_min, transit_days_max, valid_from, valid_to, version_number, status, source, lock_version, carrier_id, organization_id)
VALUES
  (19, true, NOW(), NOW(), 'LOCAL',         'standard', 0.10, 30.00,  750.00,  38.00,  2.80, 1, 2, '2025-08-01', '2026-12-31', 1, 'ACTIVE',   'MANUAL', 0, 9,  2),
  (20, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1150.00,  60.00,  4.80, 2, 5, '2025-05-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 10, 2),
  (21, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1196.00,  61.80,  4.80, 2, 5, '2025-08-01', '2026-12-31', 2, 'INACTIVE', 'MANUAL', 0, 10, 2),
  (22, true, NOW(), NOW(), 'NACIONAL',      'standard', 0.10, 50.00, 1243.84,  63.65,  4.80, 2, 5, '2025-11-01', '2026-12-31', 3, 'ACTIVE',   'MANUAL', 0, 10, 2),
  (23, true, NOW(), NOW(), 'INTERNACIONAL', 'standard', 0.10, 40.00, 2900.00, 110.00,  9.50, 4, 8, '2025-06-01', '2026-12-31', 1, 'INACTIVE', 'IMPORT', 0, 11, 2),
  (24, true, NOW(), NOW(), 'INTERNACIONAL', 'standard', 0.10, 40.00, 3016.00, 113.30,  9.50, 4, 8, '2025-09-01', '2026-12-31', 2, 'ACTIVE',   'API',    0, 11, 2),
  (25, true, NOW(), NOW(), 'NACIONAL',      'express',  0.10, 20.00, 1450.00,  78.00,  6.00, 1, 3, '2025-10-01', '2027-03-31', 1, 'ACTIVE',   'IMPORT', 0, 12, 2),
  (26, true, NOW(), NOW(), 'LOCAL',         'express',  0.10, 50.00,  700.00,  36.00,  2.60, 1, 2, '2025-05-01', '2026-12-31', 1, 'INACTIVE', 'MANUAL', 0, 13, 2),
  (27, true, NOW(), NOW(), 'LOCAL',         'express',  0.10, 50.00,  728.00,  37.08,  2.60, 1, 2, '2025-08-01', '2026-12-31', 2, 'ACTIVE',   'MANUAL', 0, 13, 2)
ON DUPLICATE KEY UPDATE zone = VALUES(zone);

-- ─── SHIPPING RULES (Org 1 – Logística Andina SA) ────────────

INSERT INTO shipping_rules (id, active, created_at, updated_at, name, field, operator, value, action, action_value, priority, organization_id)
VALUES
  (3,  true,  NOW(), NOW(), 'Sobrepeso >30kg',               'WEIGHT',      'GT',  '30',            'SURCHARGE', 15.00, 5, 1),
  (4,  true,  NOW(), NOW(), 'Envío liviano descuento',        'WEIGHT',      'LT',  '2',             'DISCOUNT',  10.00, 8, 1),
  (5,  true,  NOW(), NOW(), 'Zona internacional express',     'ZONE',        'EQ',  'internacional', 'SURCHARGE', 22.00, 6, 1),
  (6,  true,  NOW(), NOW(), 'Bloquear zona internacional',    'ZONE',        'EQ',  'internacional', 'BLOCK',    100.00, 1, 1),
  (7,  true,  NOW(), NOW(), 'Priorizar express',              'SERVICE_TYPE', 'EQ',  'express',       'PREFER',     5.00, 3, 1),
  (8,  false, NOW(), NOW(), 'Recargo nacional >10kg',         'WEIGHT',      'GT',  '10',            'SURCHARGE',  7.00, 7, 1),
  -- Org 2 (Cargo Express MX)
  (9,  true,  NOW(), NOW(), 'Sobrepeso >25kg',                'WEIGHT',      'GT',  '25',            'SURCHARGE', 12.00, 5, 2),
  (10, true,  NOW(), NOW(), 'Descuento nacional',             'ZONE',        'EQ',  'nacional',      'DISCOUNT',   8.00, 6, 2),
  (11, true,  NOW(), NOW(), 'Recargo envíos express',         'SERVICE_TYPE', 'EQ',  'express',       'SURCHARGE', 25.00, 4, 2),
  (12, true,  NOW(), NOW(), 'Bloquear zona internacional',    'ZONE',        'EQ',  'internacional', 'BLOCK',    100.00, 1, 2),
  (13, true,  NOW(), NOW(), 'Recargo express adicional',      'SERVICE_TYPE', 'EQ',  'express',       'SURCHARGE', 18.00, 4, 2)
ON DUPLICATE KEY UPDATE name = VALUES(name);
