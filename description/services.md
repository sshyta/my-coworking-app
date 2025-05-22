## auth-service

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path            | Description                     | Request Body                                              | Response Body                                                                      | Auth         |
|--------|-----------------|---------------------------------|-----------------------------------------------------------|------------------------------------------------------------------------------------|--------------|
| POST   | `/auth/login`   | Authenticate user, issue tokens | `{ "email": string, "password": string }`                 | `{ "accessToken": string, "refreshToken": string, "expiresIn": number }`           | Public       |
| POST   | `/auth/refresh` | Refresh access token            | `{ "refreshToken": string }`                              | `{ "accessToken": string, "expiresIn": number }`                                   | Public       |
| POST   | `/auth/logout`  | Revoke refresh token            | `{ "refreshToken": string }`                              | *204 No Content*                                                                   | Bearer       |
| GET    | `/users`        | List users                      | —                                                         | `[ { "id": UUID, "email": string, "role": string, "isActive": boolean, ... }, … ]` | Bearer/Admin |
| POST   | `/users`        | Create new user                 | `{ "email": string, "password": string, "role": string }` | `{ "id": UUID, "email": string, "role": string, ... }`                             | Bearer/Admin |
| GET    | `/users/{id}`   | Get user by ID                  | —                                                         | `{ "id": UUID, "email": string, "role": string, ... }`                             | Bearer       |
| PUT    | `/users/{id}`   | Update user                     | Partial user object                                       | `{ "id": UUID, ... }`                                                              | Bearer/Admin |
| DELETE | `/users/{id}`   | Soft-delete user                | —                                                         | *204 No Content*                                                                   | Bearer/Admin |

---

## workspace-service

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                                  | Description                               | Request Body                                                    | Response Body                                                                   | Auth         |
|--------|---------------------------------------|-------------------------------------------|-----------------------------------------------------------------|---------------------------------------------------------------------------------|--------------|
| GET    | `/workspaces`                         | List all workspaces                       | —                                                               | `[ { "id": UUID, "name": string, "address": object, "timeZone": string }, … ]`  | Bearer       |
| POST   | `/workspaces`                         | Create a workspace                        | `{ "name": string, "address": {...}, "timeZone": string }`      | `{ "id": UUID, ... }`                                                           | Bearer/Admin |
| GET    | `/workspaces/{workspaceId}`           | Get a workspace by ID                     | —                                                               | `{ "id": UUID, "name": string, ... }`                                           | Bearer       |
| PUT    | `/workspaces/{workspaceId}`           | Update workspace                          | Partial workspace object                                        | `{ "id": UUID, ... }`                                                           | Bearer/Admin |
| DELETE | `/workspaces/{workspaceId}`           | Delete workspace                          | —                                                               | *204 No Content*                                                                | Bearer/Admin |
| GET    | `/workspaces/{workspaceId}/resources` | List resources in a workspace             | —                                                               | `[ { "id": UUID, "label": string, "type": string, "status": string, ... }, … ]` | Bearer       |
| GET    | `/resources`                          | Search/filter resources across all spaces | Query params: `?type=&status=&workspaceId=`                     | `[ {...}, … ]`                                                                  | Bearer       |
| POST   | `/resources`                          | Create a resource                         | `{ "workspaceId": UUID, "label": string, "type": string, ... }` | `{ "id": UUID, ... }`                                                           | Bearer/Admin |
| PUT    | `/resources/{resourceId}`             | Update resource                           | Partial resource object                                         | `{ "id": UUID, ... }`                                                           | Bearer/Admin |
| DELETE | `/resources/{resourceId}`             | Delete resource                           | —                                                               | *204 No Content*                                                                | Bearer/Admin |

---

## booking-service

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                 | Description                        | Request Body                                                                                           | Response Body                                                  | Auth   |
|--------|----------------------|------------------------------------|--------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|--------|
| GET    | `/reservations`      | List all reservations              | Query params: `?userId=&status=&from=&to=`                                                             | `[ { "id": UUID, "userId": UUID, "status": string, ... }, … ]` | Bearer |
| POST   | `/reservations`      | Create a reservation               | `{ "userId": UUID, "items": [ { "resourceId": UUID, "start": ISO, "end": ISO } ], "currency": "RUB" }` | `{ "id": UUID, "totalPrice": number, "currency": "RUB", ... }` | Bearer |
| GET    | `/reservations/{id}` | Get reservation by ID              | —                                                                                                      | `{ "id": UUID, "status": string, "items": [ … ], ... }`        | Bearer |
| PUT    | `/reservations/{id}` | Update reservation status or dates | `{ "status": string }`                                                                                 | `{ "id": UUID, "status": string, ... }`                        | Bearer |
| DELETE | `/reservations/{id}` | Cancel reservation                 | —                                                                                                      | *204 No Content*                                               | Bearer |

> **Kafka topics**
> - `reservation.created` → payload: reservation summary
> - `reservation.confirmed` → after payment
> - `reservation.cancelled`

---

## payment-service

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                    | Description                       | Request Body                                                                      | Response Body                                                    | Auth   |
|--------|-------------------------|-----------------------------------|-----------------------------------------------------------------------------------|------------------------------------------------------------------|--------|
| GET    | `/invoices`             | List invoices                     | Query params: `?reservationId=&status=&dueDate=`                                  | `[ { "id": UUID, "amount": number, "status": string, ... }, … ]` | Bearer |
| POST   | `/invoices`             | Create invoice                    | `{ "reservationId": UUID, "amount": number, "currency": string, "dueDate": ISO }` | `{ "id": UUID, "status": "new", ... }`                           | Bearer |
| GET    | `/invoices/{id}`        | Get invoice by ID                 | —                                                                                 | `{ "id": UUID, "amount": number, ... }`                          | Bearer |
| PUT    | `/invoices/{id}`        | Update invoice status or due date | `{ "status": string }`                                                            | `{ "id": UUID, "status": string, ... }`                          | Bearer |
| GET    | `/payment-methods`      | List saved payment methods        | —                                                                                 | `[ { "id": UUID, "type": string, ... }, … ]`                     | Bearer |
| POST   | `/payment-methods`      | Add a payment method              | `{ "userId": UUID, "type": string, "token": string }`                             | `{ "id": UUID, ... }`                                            | Bearer |
| DELETE | `/payment-methods/{id}` | Remove payment method             | —                                                                                 | *204 No Content*                                                 | Bearer |
| POST   | `/payments`             | Execute a payment                 | `{ "invoiceId": UUID, "paymentMethodId": UUID }`                                  | `{ "id": UUID, "status": string, "externalRef": string, ... }`   | Bearer |

> **Kafka topics**
> - `invoice.issued`
> - `invoice.paid`
> - `payment.failed`

---

## guest-access-service

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                 | Description              | Request Body                                                                                          | Response Body                                                 | Auth   |
|--------|----------------------|--------------------------|-------------------------------------------------------------------------------------------------------|---------------------------------------------------------------|--------|
| POST   | `/guest-passes`      | Request a guest pass     | `{ "hostUserId": UUID, "guestName": string, "guestEmail": string, "startTime": ISO, "endTime": ISO }` | `{ "id": UUID, "qrToken": string, "status": "pending", ... }` | Bearer |
| GET    | `/guest-passes/{id}` | Get guest pass details   | —                                                                                                     | `{ "id": UUID, "status": string, "qrToken": string, ... }`    | Bearer |
| PUT    | `/guest-passes/{id}` | Update guest pass status | `{ "status": string }`                                                                                | `{ "id": UUID, "status": string, ... }`                       | Bearer |
| POST   | `/visit-logs`        | Log guest entry/exit     | `{ "guestPassId": UUID, "gateId": UUID, "event": "enter" \| "exit" }`                                 | `{ "id": UUID, "entryTime": ISO, "exitTime": ISO, ... }`      | Bearer |

---

## parking-service

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                         | Description                        | Request Body                                                                              | Response Body                                                        | Auth         |
|--------|------------------------------|------------------------------------|-------------------------------------------------------------------------------------------|----------------------------------------------------------------------|--------------|
| GET    | `/parking-spots`             | List all parking spots             | Query params: `?workspaceId=&status=&type=`                                               | `[ { "id": UUID, "spotNumber": number, "status": string, ... }, … ]` | Bearer       |
| POST   | `/parking-spots`             | Create parking spot                | `{ "workspaceId": UUID, "spotNumber": number, "type": string }`                           | `{ "id": UUID, ... }`                                                | Bearer/Admin |
| POST   | `/vehicles`                  | Register vehicle                   | `{ "userId": UUID, "plateNumber": string, "type": string }`                               | `{ "id": UUID, ... }`                                                | Bearer       |
| GET    | `/parking-reservations`      | List reservations                  | Query params: `?userId=&spotId=&status=`                                                  | `[ { "id": UUID, "startTime": ISO, "endTime": ISO, ... }, … ]`       | Bearer       |
| POST   | `/parking-reservations`      | Reserve a spot                     | `{ "userId": UUID, "vehicleId": UUID, "spotId": UUID, "startTime": ISO, "endTime": ISO }` | `{ "id": UUID, "status": "active", ... }`                            | Bearer       |
| PUT    | `/parking-reservations/{id}` | Update reservation status or times | `{ "status": string }`                                                                    | `{ "id": UUID, "status": string, ... }`                              | Bearer       |
| DELETE | `/parking-reservations/{id}` | Cancel reservation                 | —                                                                                         | *204 No Content*                                                     | Bearer       |

---

## order-service

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path             | Description                      | Request Body                                                                                      | Response Body                                                     | Auth         |
|--------|------------------|----------------------------------|---------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|--------------|
| GET    | `/products`      | List all products                | Query params: `?status=&minStock=`                                                                | `[ { "id": UUID, "name": string, "unitPrice": number, ... }, … ]` | Bearer       |
| POST   | `/products`      | Create a product                 | `{ "name": string, "unitPrice": number, "stockQty": number }`                                     | `{ "id": UUID, ... }`                                             | Bearer/Admin |
| PUT    | `/products/{id}` | Update product                   | Partial product object                                                                            | `{ "id": UUID, ... }`                                             | Bearer/Admin |
| DELETE | `/products/{id}` | Deactivate product (soft delete) | —                                                                                                 | *204 No Content*                                                  | Bearer/Admin |
| GET    | `/orders`        | List orders                      | Query params: `?userId=&status=`                                                                  | `[ { "id": UUID, "totalAmount": number, ... }, … ]`               | Bearer       |
| POST   | `/orders`        | Create an order                  | `{ "userId": UUID, "workspaceId": UUID, "items": [ { "productId": UUID, "quantity": number } ] }` | `{ "id": UUID, "totalAmount": number, "status": "new", ... }`     | Bearer       |
| GET    | `/orders/{id}`   | Get order details                | —                                                                                                 | `{ "id": UUID, "items": [ … ], "status": string, ... }`           | Bearer       |
| PUT    | `/orders/{id}`   | Update order status              | `{ "status": string }`                                                                            | `{ "id": UUID, "status": string, ... }`                           | Bearer       |

> **Kafka topics**
> - `order.created`
> - `order.completed`

---

## support-service

**Protocol**: HTTPS/REST + WebSocket (real-time chat)  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                     | Description                   | Request Body                                                   | Response Body                                      | Auth           |
|--------|--------------------------|-------------------------------|----------------------------------------------------------------|----------------------------------------------------|----------------|
| GET    | `/tickets`               | List support tickets          | Query params: `?userId=&status=&priority=`                     | `[ { "id": UUID, "subject": string, ... }, … ]`    | Bearer         |
| POST   | `/tickets`               | Open new ticket               | `{ "userId": UUID, "subject": string, "description": string }` | `{ "id": UUID, "status": "open", ... }`            | Bearer         |
| GET    | `/tickets/{id}`          | Get ticket by ID              | —                                                              | `{ "id": UUID, "messages": [ … ], ... }`           | Bearer         |
| POST   | `/tickets/{id}/messages` | Add message to ticket         | `{ "senderId": UUID, "text": string }`                         | `{ "id": UUID, "text": string, "createdAt": ISO }` | Bearer         |
| PUT    | `/tickets/{id}`          | Update ticket status/priority | `{ "status": string, "priority": string }`                     | `{ "id": UUID, "status": string, ... }`            | Bearer/Support |

> **WebSocket**: `/ws/support/{ticketId}` for real-time chat

---

## security-service

**Protocol**: HTTPS/REST + MQTT (IoT gates)  
**Content-Type**: `application/json`

### Endpoints

| Method | Path             | Description            | Request Body                                                      | Response Body                                                  | Auth                 |
|--------|------------------|------------------------|-------------------------------------------------------------------|----------------------------------------------------------------|----------------------|
| GET    | `/gates`         | List all access gates  | —                                                                 | `[ { "id": UUID, "location": string, ... }, … ]`               | Bearer               |
| POST   | `/gates`         | Register a new gate    | `{ "location": string, "type": string }`                          | `{ "id": UUID, ... }`                                          | Bearer/Admin         |
| GET    | `/access-events` | List raw access events | Query params: `?gateId=&from=&to=`                                | `[ { "id": UUID, "gateId": UUID, "timestamp": ISO, ... }, … ]` | Bearer               |
| POST   | `/access-events` | Push event from gate   | `{ "gateId": UUID, "passId": UUID, "action": "enter" \| "exit" }` | `{ "id": UUID, "timestamp": ISO, ... }`                        | Bearer / MQTT client |

> **MQTT topic**: `security/gates/{gateId}/events`

---

## hr-service

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path              | Description          | Request Body                                          | Response Body                                                 | Auth      |
|--------|-------------------|----------------------|-------------------------------------------------------|---------------------------------------------------------------|-----------|
| GET    | `/employees`      | List employees       | —                                                     | `[ { "id": UUID, "name": string, "department": string }, … ]` | Bearer/HR |
| POST   | `/employees`      | Add new employee     | `{ "name": string, "email": string, "deptId": UUID }` | `{ "id": UUID, ... }`                                         | Bearer/HR |
| PUT    | `/employees/{id}` | Update employee data | Partial employee object                               | `{ "id": UUID, ... }`                                         | Bearer/HR |
| DELETE | `/employees/{id}` | Remove employee      | —                                                     | *204 No Content*                                              | Bearer/HR |
| GET    | `/departments`    | List departments     | —                                                     | `[ { "id": UUID, "name": string }, … ]`                       | Bearer/HR |
| POST   | `/departments`    | Create department    | `{ "name": string }`                                  | `{ "id": UUID, ... }`                                         | Bearer/HR |

---

## analytics-service

**Protocol**: HTTPS/REST + WebSocket (dashboards)  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                            | Description                        | Request Body                            | Response Body                                                                  | Auth         |
|--------|---------------------------------|------------------------------------|-----------------------------------------|--------------------------------------------------------------------------------|--------------|
| GET    | `/metrics`                      | Fetch system metrics (time-series) | Query params: `?metric=&from=&to=`      | `{ "metric": string, "points": [ { "timestamp": ISO, "value": number }, … ] }` | Bearer/Admin |
| GET    | `/reports/sales`                | Generate sales report              | Query params: `?from=&to=&workspaceId=` | `{ "totalSales": number, "byDay": [ … ] }`                                     | Bearer/Admin |
| GET    | `/reports/resource-utilization` | Resource usage over time           | Query params: `?resourceId=&from=&to=`  | `{ "utilization": [ … ] }`                                                     | Bearer/Admin |
| WS     | `/ws/analytics`                 | Push live metric updates           | —                                       | live JSON messages                                                             | Bearer/Admin |

---

## notification-service

**Protocol**: HTTPS/REST + WebSocket (push)  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                         | Description                  | Request Body                                            | Response Body                                                   | Auth   |
|--------|------------------------------|------------------------------|---------------------------------------------------------|-----------------------------------------------------------------|--------|
| GET    | `/notifications`             | List user notifications      | Query params: `?userId=&status=`                        | `[ { "id": UUID, "type": string, "message": string, ... }, … ]` | Bearer |
| POST   | `/notifications`             | Create/send notification     | `{ "userId": UUID, "type": string, "payload": object }` | `{ "id": UUID, ... }`                                           | Bearer |
| PUT    | `/notifications/{id}`        | Mark notification read       | `{ "status": "read" }`                                  | `{ "id": UUID, "status": "read" }`                              | Bearer |
| WS     | `/ws/notifications/{userId}` | Push real-time notifications | —                                                       | live JSON messages                                              | Bearer |

---

## api-gateway / BFF

**Protocol**: HTTPS/REST (aggregator)  
**Content-Type**: `application/json`

### Features

- **Routing**
    - All incoming `/api/{service}/{...}` proxies to the corresponding microservice.
- **Aggregation**
    - Combines data from multiple services into one response (e.g. user dashboard).
- **Authentication**
    - Validates JWT issued by `auth-service`, injects `userId` into downstream calls.
- **Rate Limiting & Caching**
    - Implements per-user rate limits, caches read-heavy endpoints.
- **Swagger/OpenAPI**
    - Consolidated API spec at `/api/docs`.

### Example Aggregation Endpoint

| Method | Path             | Description                        | Response Body                                                               | Auth   |
|--------|------------------|------------------------------------|-----------------------------------------------------------------------------|--------|
| GET    | `/api/dashboard` | User’s workspace dashboard summary | `{ "reservations": [...], "invoices": [...], "notifications": [...], ... }` | Bearer |

---