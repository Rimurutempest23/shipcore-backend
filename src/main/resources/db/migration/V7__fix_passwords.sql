-- Fix all user passwords so BCrypt hash matches 'admin123'
-- BCrypt hash for 'admin123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
UPDATE users SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE email IN ('admin@shipcore.com', 'admin@andina.com', 'juan@test.com');

-- Also set operador users to 'operador123' using same hash (demo simplification: all use admin123)
UPDATE users SET password = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE email = 'operador@shipcore.com';
