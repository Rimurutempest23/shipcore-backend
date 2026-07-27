# ShipCore - Pruebas Postman actualizadas

Base URL:

```text
http://localhost:8080
```

Variables recomendadas en Postman:

```text
baseUrl = http://localhost:8080
token =
organizationId = 1
carrierId =
```

## 1. Login

```http
POST {{baseUrl}}/api/v1/auth/login
```

Body JSON:

```json
{
  "email": "admin@shipcore.com",
  "password": "password"
}
```

Respuesta esperada: `200 OK`.

Ahora responde con token, usuario y organizacion:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer",
  "message": "Inicio de sesion exitoso.",
  "user": {
    "id": 1,
    "firstName": "Admin",
    "lastName": "Demo",
    "email": "admin@shipcore.com",
    "role": "ROLE_ADMIN"
  },
  "organization": {
    "id": 1,
    "name": "ShipCore Demo",
    "country": "PE",
    "plan": "starter",
    "softLimit": 1000,
    "hardLimit": 1200,
    "currentUsage": 0
  }
}
```

En la pestana `Tests` de Postman puedes guardar el token:

```javascript
const response = pm.response.json();
pm.environment.set("token", response.accessToken);
pm.environment.set("organizationId", response.organization.id);
```

En las demas requests usa:

```text
Authorization: Bearer {{token}}
```

## 2. Listar organizaciones

```http
GET {{baseUrl}}/api/v1/organizations
```

Respuesta esperada: `200 OK`.

Debe devolver la organizacion demo con los campos nuevos:

```json
[
  {
    "id": 1,
    "name": "ShipCore Demo",
    "ruc": "20123456789",
    "address": "Lima, Peru",
    "phone": "999999999",
    "country": "PE",
    "plan": "starter",
    "softLimit": 1000,
    "hardLimit": 1200,
    "currentUsage": 0,
    "active": true
  }
]
```

## 3. Crear organizacion

```http
POST {{baseUrl}}/api/v1/organizations
```

Body JSON actualizado:

```json
{
  "name": "Empresa Test",
  "ruc": "20987654321",
  "address": "Av. Prueba 123",
  "phone": "987654321",
  "country": "PE",
  "plan": "starter",
  "softLimit": 1000,
  "hardLimit": 1200,
  "currentUsage": 0
}
```

Respuesta esperada: `201 Created`.

En `Tests`:

```javascript
const response = pm.response.json();
pm.environment.set("organizationId", response.id);
```

## 4. Crear usuario operador

```http
POST {{baseUrl}}/api/v1/users
```

Body JSON actualizado:

```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan@test.com",
  "password": "password123",
  "role": "ROLE_OPERATOR",
  "phone": "988777666",
  "address": "Av. Usuario 456",
  "bio": "Operador de pruebas",
  "organizationId": {{organizationId}}
}
```

Respuesta esperada: `201 Created`.

## 5. Crear courier

```http
POST {{baseUrl}}/api/v1/carriers
```

Body JSON actualizado:

```json
{
  "name": "Olva Courier",
  "code": "OLVA",
  "serviceType": "EXPRESS",
  "logoUrl": null,
  "contactEmail": "contacto@olva.com",
  "phone": "999888777",
  "organizationId": {{organizationId}}
}
```

Respuesta esperada: `201 Created`.

En `Tests`:

```javascript
const response = pm.response.json();
pm.environment.set("carrierId", response.id);
```

## 6. Listar couriers

```http
GET {{baseUrl}}/api/v1/carriers
```

Respuesta esperada: `200 OK`.

Debe devolver el courier creado con `code`, `serviceType`, `logoUrl`, `organizationId` y `organizationName`.

## 7. Crear tarifa

```http
POST {{baseUrl}}/api/v1/rates
```

Body JSON actualizado:

```json
{
  "zone": "LIMA",
  "serviceType": "EXPRESS",
  "minWeightKg": 0.00,
  "maxWeightKg": 10.00,
  "basePrice": 12.50,
  "pricePerKg": 2.00,
  "pricePerKm": 0.50,
  "transitDaysMin": 1,
  "transitDaysMax": 2,
  "validFrom": "2026-07-01",
  "validTo": "2026-12-31",
  "versionNumber": 1,
  "status": "ACTIVE",
  "source": "MANUAL",
  "carrierId": {{carrierId}}
}
```

Respuesta esperada: `201 Created`.

## 8. Listar tarifas

```http
GET {{baseUrl}}/api/v1/rates
```

Respuesta esperada: `200 OK`.

Debe mostrar la tarifa con:

```json
{
  "zone": "LIMA",
  "serviceType": "EXPRESS",
  "pricePerKm": 0.50,
  "transitDaysMin": 1,
  "transitDaysMax": 2,
  "source": "MANUAL",
  "carrierId": 2,
  "carrierName": "Olva Courier",
  "organizationId": 1
}
```

## 9. Ver versiones de tarifa

```http
GET {{baseUrl}}/api/v1/rates/carrier/{{carrierId}}/versions?zone=LIMA
```

Respuesta esperada: `200 OK`.

Debe devolver las tarifas de ese courier/zona ordenadas por `versionNumber` descendente.

## 10. Probar validacion

```http
POST {{baseUrl}}/api/v1/rates
```

Body JSON actualizado para provocar error:

```json
{
  "zone": "",
  "serviceType": "",
  "minWeightKg": 20,
  "maxWeightKg": 10,
  "basePrice": 12.50,
  "pricePerKg": 2.00,
  "pricePerKm": 0.50,
  "transitDaysMin": 5,
  "transitDaysMax": 2,
  "validFrom": "2026-12-31",
  "validTo": "2026-07-01",
  "versionNumber": 1,
  "status": "ACTIVE",
  "source": "MANUAL",
  "carrierId": {{carrierId}}
}
```

Respuesta esperada: `400 Bad Request`.

## 11. Probar token faltante

```http
GET {{baseUrl}}/api/v1/carriers
```

Sin header `Authorization`.

Respuesta esperada: `401 Unauthorized`.

## 12. Probar duplicado

Vuelve a crear el mismo courier dentro de la misma organizacion:

```http
POST {{baseUrl}}/api/v1/carriers
```

```json
{
  "name": "Olva Courier",
  "code": "OLVA",
  "serviceType": "EXPRESS",
  "logoUrl": null,
  "contactEmail": "contacto@olva.com",
  "phone": "999888777",
  "organizationId": {{organizationId}}
}
```

Respuesta esperada: `409 Conflict`.

## Nota para repetir pruebas

Si ya corriste estas pruebas antes, pueden fallar por duplicados:

- `ruc`: `20987654321`
- `email`: `juan@test.com`
- courier: `Olva Courier` en la misma organizacion

Para repetirlas sin limpiar la base, cambia esos valores por otros unicos.
