-- Add CHECK constraints for enum columns in shipping_rules table
ALTER TABLE shipping_rules MODIFY COLUMN field VARCHAR(50) NOT NULL;
ALTER TABLE shipping_rules MODIFY COLUMN operator VARCHAR(50) NOT NULL;
ALTER TABLE shipping_rules MODIFY COLUMN action VARCHAR(50) NOT NULL;

-- Safe check constraint additions
ALTER TABLE shipping_rules ADD CONSTRAINT chk_shipping_rules_field CHECK (field IN ('WEIGHT', 'ZONE', 'SERVICE_TYPE', 'CARRIER'));
ALTER TABLE shipping_rules ADD CONSTRAINT chk_shipping_rules_operator CHECK (operator IN ('GT', 'LT', 'EQ', 'GTE', 'LTE', 'CONTAINS'));
ALTER TABLE shipping_rules ADD CONSTRAINT chk_shipping_rules_action CHECK (action IN ('SURCHARGE', 'DISCOUNT', 'BLOCK', 'PREFER'));
