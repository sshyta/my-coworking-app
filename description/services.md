# API Documentation

Ниже описаны все 13 микросервисов системы управления сетью коворкингов. В каждом разделе:

1. Краткое описание возможностей сервиса.
2. Спецификация API (протокол, методы, пути, тела запросов/ответов).
3. Все идентификаторы (`id`, `userId`, `workspaceId` и т.д.) имеют тип `int`.

---

## 1. auth-service

**Описание:** Отвечает за регистрацию и аутентификацию пользователей, выдачу и обновление JWT-токенов.

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path            | Описание                                  | Request Body                                                  | Response Body                                                                   | Auth         |
|--------|-----------------|-------------------------------------------|---------------------------------------------------------------|---------------------------------------------------------------------------------|--------------|
| POST   | `/auth/login`   | Аутентификация, выдача токенов            | `{ "email": string, "password": string }`                     | `{ "accessToken": string, "refreshToken": string, "expiresIn": number }`        | Public       |
| POST   | `/auth/refresh` | Обновление access-токена                  | `{ "refreshToken": string }`                                  | `{ "accessToken": string, "expiresIn": number }`                                | Public       |
| POST   | `/auth/logout`  | Отзыв refresh-токена                      | `{ "refreshToken": string }`                                  | *204 No Content*                                                                | Bearer       |
| GET    | `/users`        | Список пользователей                      | —                                                             | `[ { "id": int, "email": string, "role": string, "isActive": boolean, … }, … ]` | Bearer/Admin |
| POST   | `/users`        | Создать пользователя                      | `{ "email": string, "password": string, "role": string }`     | `{ "id": int, "email": string, "role": string, … }`                             | Bearer/Admin |
| GET    | `/users/{id}`   | Получить пользователя по ID               | —                                                             | `{ "id": int, "email": string, "role": string, … }`                             | Bearer       |
| PUT    | `/users/{id}`   | Обновить данные пользователя              | `{ "email"?: string, "role"?: string, "isActive"?: boolean }` | `{ "id": int, "email": string, "role": string, … }`                             | Bearer/Admin |
| DELETE | `/users/{id}`   | Деактивировать (soft-delete) пользователя | —                                                             | *204 No Content*                                                                | Bearer/Admin |

---

## 2. workspace-service

**Описание:** Управление коворкинг-пространствами и их ресурсами (рабочие места, комнаты, принтеры и т.д.).

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                         | Описание                               | Request Body                                                  | Response Body                                                                 | Auth         |
|--------|------------------------------|----------------------------------------|---------------------------------------------------------------|-------------------------------------------------------------------------------|--------------|
| GET    | `/workspaces`                | Список всех коворкингов                | —                                                             | `[ { "id": int, "name": string, "address": object, "timeZone": string }, … ]` | Bearer       |
| POST   | `/workspaces`                | Создать коворкинг                      | `{ "name": string, "address": {...}, "timeZone": string }`    | `{ "id": int, "name": string, … }`                                            | Bearer/Admin |
| GET    | `/workspaces/{id}`           | Получить коворкинг по ID               | —                                                             | `{ "id": int, "name": string, "address": object, "timeZone": string, … }`     | Bearer       |
| PUT    | `/workspaces/{id}`           | Обновить коворкинг                     | `{ "name"?: string, "address"?: {...}, "timeZone"?: string }` | `{ "id": int, "name": string, … }`                                            | Bearer/Admin |
| DELETE | `/workspaces/{id}`           | Удалить коворкинг                      | —                                                             | *204 No Content*                                                              | Bearer/Admin |
| GET    | `/workspaces/{id}/resources` | Список ресурсов внутри коворкинга      | —                                                             | `[ { "id": int, "label": string, "type": string, "status": string, … }, … ]`  | Bearer       |
| GET    | `/resources`                 | Поиск/фильтрация ресурсов по всей сети | Query params: `?type=&status=&workspaceId=`                   | `[ { "id": int, "workspaceId": int, "label": string, … }, … ]`                | Bearer       |
| POST   | `/resources`                 | Создать ресурс                         | `{ "workspaceId": int, "label": string, "type": string, … }`  | `{ "id": int, "workspaceId": int, "label": string, … }`                       | Bearer/Admin |
| PUT    | `/resources/{id}`            | Обновить ресурс                        | `{ "label"?: string, "status"?: string, … }`                  | `{ "id": int, "label": string, … }`                                           | Bearer/Admin |
| DELETE | `/resources/{id}`            | Удалить ресурс                         | —                                                             | *204 No Content*                                                              | Bearer/Admin |

---

## 3. booking-service

**Описание:** Управление бронированиями ресурсов коворкинга.

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                 | Описание                    | Request Body                                                                                          | Response Body                                                | Auth   |
|--------|----------------------|-----------------------------|-------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|--------|
| GET    | `/reservations`      | Список бронирований         | Query params: `?userId=&status=&from=&to=`                                                            | `[ { "id": int, "userId": int, "status": string, … }, … ]`   | Bearer |
| POST   | `/reservations`      | Создать бронирование        | `{ "userId": int, "items": [ { "resourceId": int, "start": ISO, "end": ISO } ], "currency": string }` | `{ "id": int, "totalPrice": number, "currency": string, … }` | Bearer |
| GET    | `/reservations/{id}` | Получить бронирование по ID | —                                                                                                     | `{ "id": int, "status": string, "items":[…], … }`            | Bearer |
| PUT    | `/reservations/{id}` | Обновить статус или даты    | `{ "status"?: string, "start"?: ISO, "end"?: ISO }`                                                   | `{ "id": int, "status": string, … }`                         | Bearer |
| DELETE | `/reservations/{id}` | Отменить бронирование       | —                                                                                                     | *204 No Content*                                             | Bearer |

> **Kafka topics**
> - `reservation.created`
> - `reservation.confirmed`
> - `reservation.cancelled`

---

## 4. payment-service

**Описание:** Выставление счетов, приём и учёт платежей, интеграция с платёжными провайдерами.

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                    | Описание                        | Request Body                                                                     | Response Body                                                 | Auth   |
|--------|-------------------------|---------------------------------|----------------------------------------------------------------------------------|---------------------------------------------------------------|--------|
| GET    | `/invoices`             | Список счетов                   | Query params: `?reservationId=&status=&dueDate=`                                 | `[ { "id": int, "amount": number, "status": string, … }, … ]` | Bearer |
| POST   | `/invoices`             | Создать счёт                    | `{ "reservationId": int, "amount": number, "currency": string, "dueDate": ISO }` | `{ "id": int, "status": "new", … }`                           | Bearer |
| GET    | `/invoices/{id}`        | Получить счёт по ID             | —                                                                                | `{ "id": int, "amount": number, … }`                          | Bearer |
| PUT    | `/invoices/{id}`        | Обновить статус или срок оплаты | `{ "status"?: string, "dueDate"?: ISO }`                                         | `{ "id": int, "status": string, … }`                          | Bearer |
| GET    | `/payment-methods`      | Список способов оплаты          | —                                                                                | `[ { "id": int, "type": string, … }, … ]`                     | Bearer |
| POST   | `/payment-methods`      | Добавить способ оплаты          | `{ "userId": int, "type": string, "token": string }`                             | `{ "id": int, "type": string, … }`                            | Bearer |
| DELETE | `/payment-methods/{id}` | Удалить способ оплаты           | —                                                                                | *204 No Content*                                              | Bearer |
| POST   | `/payments`             | Провести платёж                 | `{ "invoiceId": int, "paymentMethodId": int }`                                   | `{ "id": int, "status": string, "externalRef": string, … }`   | Bearer |

> **Kafka topics**
> - `invoice.issued`
> - `invoice.paid`
> - `payment.failed`

---

## 5. guest-access-service

**Описание:** Управление гостевыми пропусками и учёт посещений.

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                 | Описание                       | Request Body                                                                                         | Response Body                                              | Auth   |
|--------|----------------------|--------------------------------|------------------------------------------------------------------------------------------------------|------------------------------------------------------------|--------|
| POST   | `/guest-passes`      | Запрос гостевого пропуска      | `{ "hostUserId": int, "guestName": string, "guestEmail": string, "startTime": ISO, "endTime": ISO }` | `{ "id": int, "qrToken": string, "status": "pending", … }` | Bearer |
| GET    | `/guest-passes/{id}` | Детали гостевого пропуска      | —                                                                                                    | `{ "id": int, "guestName": string, "status": string, … }`  | Bearer |
| PUT    | `/guest-passes/{id}` | Обновить статус пропуска       | `{ "status": string }`                                                                               | `{ "id": int, "status": string, … }`                       | Bearer |
| POST   | `/visit-logs`        | Логирование входа/выхода гостя | `{ "guestPassId": int, "gateId": int, "event": "enter" \| "exit" }`                                  | `{ "id": int, "entryTime": ISO, "exitTime": ISO, … }`      | Bearer |

---

## 6. parking-service

**Описание:** Управление парковочными местами, транспортными средствами и их бронированием.

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                         | Описание                       | Request Body                                                                           | Response Body                                                     | Auth         |
|--------|------------------------------|--------------------------------|----------------------------------------------------------------------------------------|-------------------------------------------------------------------|--------------|
| GET    | `/parking-spots`             | Список мест на всех парковках  | Query params: `?workspaceId=&status=&type=`                                            | `[ { "id": int, "spotNumber": number, "status": string, … }, … ]` | Bearer       |
| POST   | `/parking-spots`             | Добавить новое место           | `{ "workspaceId": int, "spotNumber": number, "type": string }`                         | `{ "id": int, "spotNumber": number, … }`                          | Bearer/Admin |
| POST   | `/vehicles`                  | Зарегистрировать транспорт     | `{ "userId": int, "plateNumber": string, "type": string }`                             | `{ "id": int, "plateNumber": string, … }`                         | Bearer       |
| GET    | `/parking-reservations`      | Список бронирований машиномест | Query params: `?userId=&spotId=&status=`                                               | `[ { "id": int, "startTime": ISO, "endTime": ISO, … }, … ]`       | Bearer       |
| POST   | `/parking-reservations`      | Забронировать место            | `{ "userId": int, "vehicleId": int, "spotId": int, "startTime": ISO, "endTime": ISO }` | `{ "id": int, "status": "active", … }`                            | Bearer       |
| PUT    | `/parking-reservations/{id}` | Обновить бронь                 | `{ "status"?: string, "startTime"?: ISO, "endTime"?: ISO }`                            | `{ "id": int, "status": string, … }`                              | Bearer       |
| DELETE | `/parking-reservations/{id}` | Отменить бронь                 | —                                                                                      | *204 No Content*                                                  | Bearer       |

---

## 7. order-service

**Описание:** Заказ канцелярии, техники, еды и прочих дополнительных услуг.

**Protocol**: HTTPS/REST + Kafka events  
**Content-Type**: `application/json`

### Endpoints

| Method | Path             | Описание               | Request Body                                                                                   | Response Body                                                  | Auth         |
|--------|------------------|------------------------|------------------------------------------------------------------------------------------------|----------------------------------------------------------------|--------------|
| GET    | `/products`      | Список товаров         | Query params: `?status=&minStock=`                                                             | `[ { "id": int, "name": string, "unitPrice": number, … }, … ]` | Bearer       |
| POST   | `/products`      | Добавить новый товар   | `{ "name": string, "unitPrice": number, "stockQty": number }`                                  | `{ "id": int, "name": string, … }`                             | Bearer/Admin |
| PUT    | `/products/{id}` | Обновить данные товара | `{ "name"?: string, "unitPrice"?: number, "stockQty"?: number, "status"?: string }`            | `{ "id": int, "name": string, … }`                             | Bearer/Admin |
| DELETE | `/products/{id}` | Деактивировать товар   | —                                                                                              | *204 No Content*                                               | Bearer/Admin |
| GET    | `/orders`        | Список всех заказов    | Query params: `?userId=&status=`                                                               | `[ { "id": int, "totalAmount": number, … }, … ]`               | Bearer       |
| POST   | `/orders`        | Создать заказ          | `{ "userId": int, "workspaceId": int, "items": [ { "productId": int, "quantity": number } ] }` | `{ "id": int, "totalAmount": number, "status": "new", … }`     | Bearer       |
| GET    | `/orders/{id}`   | Детали заказа по ID    | —                                                                                              | `{ "id": int, "items":[…], "status": string, … }`              | Bearer       |
| PUT    | `/orders/{id}`   | Обновить статус заказа | `{ "status": string }`                                                                         | `{ "id": int, "status": string, … }`                           | Bearer       |

> **Kafka topics**
> - `order.created`
> - `order.completed`

---

## 8. support-service

**Описание:** Тикетная система и real-time чат поддержки пользователей и администраторов.

**Protocol**: HTTPS/REST + WebSocket  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                     | Описание                         | Request Body                                                  | Response Body                                        | Auth           |
|--------|--------------------------|----------------------------------|---------------------------------------------------------------|------------------------------------------------------|----------------|
| GET    | `/tickets`               | Список тикетов                   | Query params: `?userId=&status=&priority=`                    | `[ { "id": int, "subject": string, … }, … ]`         | Bearer         |
| POST   | `/tickets`               | Открыть новый тикет              | `{ "userId": int, "subject": string, "description": string }` | `{ "id": int, "status": "open", … }`                 | Bearer         |
| GET    | `/tickets/{id}`          | Получить тикет по ID             | —                                                             | `{ "id": int, "messages":[…], "status": string, … }` | Bearer         |
| POST   | `/tickets/{id}/messages` | Добавить сообщение в тикет       | `{ "senderId": int, "text": string }`                         | `{ "id": int, "text": string, "createdAt": ISO, … }` | Bearer         |
| PUT    | `/tickets/{id}`          | Обновить статус/приоритет тикета | `{ "status"?: string, "priority"?: string }`                  | `{ "id": int, "status": string, … }`                 | Bearer/Support |

> **WebSocket**: `wss://…/ws/support/{ticketId}` для real-time чата

---

## 9. security-service

**Описание:** СКУД: учёт устройств, событий доступа, генерация оповещений и интеграция с IoT-гейтами.

**Protocol**: HTTPS/REST + MQTT  
**Content-Type**: `application/json`

### Endpoints

| Method | Path             | Описание                      | Request Body                                                    | Response Body                                              | Auth          |
|--------|------------------|-------------------------------|-----------------------------------------------------------------|------------------------------------------------------------|---------------|
| GET    | `/gates`         | Список всех контрольных точек | —                                                               | `[ { "id": int, "location": string, … }, … ]`              | Bearer        |
| POST   | `/gates`         | Зарегистрировать новую точку  | `{ "location": string, "type": string }`                        | `{ "id": int, "location": string, … }`                     | Bearer/Admin  |
| GET    | `/access-events` | Сырые события доступа         | Query params: `?gateId=&from=&to=`                              | `[ { "id": int, "gateId": int, "timestamp": ISO, … }, … ]` | Bearer        |
| POST   | `/access-events` | Запись события от IoT-гейта   | `{ "gateId": int, "passId": int, "action": "enter" \| "exit" }` | `{ "id": int, "timestamp": ISO, … }`                       | Bearer / MQTT |

> **MQTT topic**: `security/gates/{gateId}/events`

---

## 10. hr-service

**Описание:** Учёт сотрудников, отделов и их графиков смен.

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Endpoints

| Method | Path              | Описание                   | Request Body                                                  | Response Body                                                 | Auth      |
|--------|-------------------|----------------------------|---------------------------------------------------------------|---------------------------------------------------------------|-----------|
| GET    | `/employees`      | Список сотрудников         | —                                                             | `[ { "id": int, "name": string, "position": string, … }, … ]` | Bearer/HR |
| POST   | `/employees`      | Добавить сотрудника        | `{ "name": string, "email": string, "department": string }`   | `{ "id": int, "name": string, … }`                            | Bearer/HR |
| PUT    | `/employees/{id}` | Обновить данные сотрудника | `{ "name"?: string, "position"?: string, "status"?: string }` | `{ "id": int, "name": string, … }`                            | Bearer/HR |
| DELETE | `/employees/{id}` | Удалить сотрудника         | —                                                             | *204 No Content*                                              | Bearer/HR |
| GET    | `/departments`    | Список отделов             | —                                                             | `[ { "id": int, "name": string }, … ]`                        | Bearer/HR |
| POST   | `/departments`    | Создать отдел              | `{ "name": string }`                                          | `{ "id": int, "name": string }`                               | Bearer/HR |

---

## 11. analytics-service

**Описание:** Сбор и агрегация метрик, построение отчётов и дашбордов.

**Protocol**: HTTPS/REST + WebSocket  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                            | Описание                          | Request Body                            | Response Body                                                                 | Auth         |
|--------|---------------------------------|-----------------------------------|-----------------------------------------|-------------------------------------------------------------------------------|--------------|
| GET    | `/metrics`                      | Временные ряды метрик             | Query params: `?metric=&from=&to=`      | `{ "metric": string, "points":[ { "timestamp": ISO, "value": number }, … ] }` | Bearer/Admin |
| GET    | `/reports/sales`                | Отчёт по продажам                 | Query params: `?from=&to=&workspaceId=` | `{ "totalSales": number, "byDay":[ … ] }`                                     | Bearer/Admin |
| GET    | `/reports/resource-utilization` | Использование ресурсов по времени | Query params: `?resourceId=&from=&to=`  | `{ "utilization":[ … ] }`                                                     | Bearer/Admin |
| WS     | `/ws/analytics`                 | Live-обновления метрик            | —                                       | WebSocket-сообщения с JSON-payload                                            | Bearer/Admin |

---

## 12. notification-service

**Описание:** Управление шаблонами уведомлений и доставка push/email/SMS/ин-апп уведомлений.

**Protocol**: HTTPS/REST + WebSocket  
**Content-Type**: `application/json`

### Endpoints

| Method | Path                         | Описание                        | Request Body                                                        | Response Body                                                        | Auth   |
|--------|------------------------------|---------------------------------|---------------------------------------------------------------------|----------------------------------------------------------------------|--------|
| GET    | `/templates`                 | Список шаблонов                 | —                                                                   | `[ { "id": int, "name": string, "channel": string, … }, … ]`         | Bearer |
| POST   | `/templates`                 | Создать шаблон                  | `{ "name": string, "channel": string, "body": string }`             | `{ "id": int, "name": string, … }`                                   | Bearer |
| PUT    | `/templates/{id}`            | Обновить шаблон                 | `{ "name"?: string, "body"?: string }`                              | `{ "id": int, "name": string, … }`                                   | Bearer |
| DELETE | `/templates/{id}`            | Удалить шаблон                  | —                                                                   | *204 No Content*                                                     | Bearer |
| GET    | `/notifications`             | Список уведомлений пользователя | Query params: `?userId=&status=`                                    | `[ { "id": int, "recipientName": string, "status": string, … }, … ]` | Bearer |
| POST   | `/notifications`             | Отправить уведомление           | `{ "templateId": int, "recipientName": string, "payload": object }` | `{ "id": int, "status": string, … }`                                 | Bearer |
| PUT    | `/notifications/{id}`        | Пометить как прочитанное        | `{ "status": "read" }`                                              | `{ "id": int, "status": "read" }`                                    | Bearer |
| WS     | `/ws/notifications/{userId}` | Live-push уведомления           | —                                                                   | WebSocket-сообщения с JSON                                           | Bearer |

---

## 13. api-gateway / BFF

**Описание:** Единая точка входа для всех клиентов; маршрутизация, аутентификация, агрегация ответов и генерация сводной
OpenAPI-документации.

**Protocol**: HTTPS/REST  
**Content-Type**: `application/json`

### Features

- **Routing**: `/api/{service}/{resource}` → соответствующий микросервис.
- **Authentication**: проверка JWT (auth-service), проксирование `userId` в заголовках.
- **Aggregation**: комбинирует ответы нескольких сервисов (например, `/api/dashboard`).
- **Rate Limiting & Caching**: per-user лимиты и кэширование часто запрашиваемых GET.
- **Swagger / OpenAPI**: единый спек под `/api/docs`.

### Example Aggregation

| Method | Path             | Описание                        | Response Body                                                       | Auth   |
|--------|------------------|---------------------------------|---------------------------------------------------------------------|--------|
| GET    | `/api/dashboard` | Сводная информация пользователя | `{ "reservations": […], "invoices": […], "notifications": […], … }` | Bearer |
