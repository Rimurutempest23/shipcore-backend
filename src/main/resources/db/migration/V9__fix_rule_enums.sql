-- Fix enum value for SERVICE_TYPE in shipping_rules table
UPDATE shipping_rules SET field = 'SERVICE_TYPE' WHERE field = 'SERVICETYPE';
