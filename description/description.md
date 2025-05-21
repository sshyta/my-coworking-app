# Сводка микросервисов с сущностями (MVP)

Ниже — список сервисов, сгруппированных по статусу документации, с указанием необходимых для MVP сущностей и отметкой, какие из них уже описаны.

---

## ✅ Описанные сервисы (готовы к реализации)

### 1. **auth-service**
**Назначение:** аутентификация/авторизация, управление JWT-сессиями.  
**MVP-сущности:**
- `users` (`id`, `email`, `password_hash`, `role`, `status`, `created_at`)
- `sessions` (`id`, `user_id`, `refresh_token_hash`, `issued_at`, `expires_at`, `revoked`)

_Описание сущностей завершено._

---

### 2. **workspace-service**
**Назначение:** каталог воркспейсов и ресурсов (мест, техники).  
**MVP-сущности:**
- `workspaces` (`id`, `name`, `address`, `time_zone`, `created_at`, `updated_at`)
- `seats` (переименовать в `resources` при необходимости; поля: `id`, `workspace_id`, `floor`, `label`, `type`, `status`, `created_at`, `updated_at`)

_Описание сущностей завершено._

---

### 3. **booking-service**
**Назначение:** бронирование любых ресурсов.  
**MVP-сущности:**
- `reservations` (`id`, `user_id`, `start_time`, `end_time`, `status`)
- `booking_items` (`id`, `reservation_id`, `resource_id`, `resource_type`)

_Описание сущностей завершено._

---

## 📝 Сервисы, требующие описания и их MVP-сущности

### 4. **payment-service**
**Назначение:** выставление инвойсов, приём платежей, методы оплаты.  
**MVP-сущности:**
- `invoices` (`id`, `reservation_id`, `amount`, `currency`, `due_date`, `status`)
- `payments` (`id`, `invoice_id`, `provider`, `status`, `external_ref`)
- `payment_methods` (`id`, `user_id`, `type`, `details`)

---

### 5. **guest-access-service**
**Назначение:** выпуск и верификация гостевых пропусков.  
**MVP-сущности:**
- `guest_passes` (`id`, `guest_id`, `workspace_id`, `valid_from`, `valid_to`, `qr_code`)
- `guests` (`id`, `name`, `email`, `phone`)
- `guest_access_logs` (`id`, `guest_pass_id`, `event_time`, `event_type`)

---

### 6. **parking-service**
**Назначение:** бронирование и оплата парковочных мест.  
**MVP-сущности:**
- `parking_lots` (`id`, `workspace_id`, `address`, `capacity`)
- `parking_slots` (`id`, `parking_lot_id`, `label`, `type`, `status`)
- `parking_tariffs` (`id`, `parking_lot_id`, `rate_per_hour`, `free_period`)
- `parking_orders` (`id`, `user_id`, `parking_slot_id`, `from_time`, `to_time`, `amount`, `status`)

---

### 7. **order-service**
**Назначение:** заказы дополнительных услуг (канцтовары, еда, клининг, техника).  
**MVP-сущности:**
- `orders` (`id`, `user_id`, `total_amount`, `status`, `created_at`)
- `order_lines` (`id`, `order_id`, `service_item_id`, `quantity`, `price`)
- `service_items` (`id`, `code`, `name`, `price`, `type`)

---

### 8. **support-service**
**Назначение:** техподдержка и обращения по организации мероприятий.  
**MVP-сущности:**
- `tickets` (`id`, `user_id`, `category`, `priority`, `status`, `created_at`)
- `comments` (`id`, `ticket_id`, `author_id`, `body`, `created_at`)

---

### 9. **security-service**
**Назначение:** СКУД — видеонаблюдение, контроль доступа, тревоги.  
**MVP-сущности:**
- `devices` (`id`, `workspace_id`, `type`, `location`)
- `access_events` (`id`, `device_id`, `subject_id`, `event_time`, `result`)
- `alerts` (`id`, `access_event_id`, `severity`, `resolved`)

---

### 10. **hr-service**
**Назначение:** управление административным персоналом и сменами.  
**MVP-сущности:**
- `employees` (`id`, `name`, `position`, `hired_at`, `status`)
- `shifts` (`id`, `employee_id`, `start_time`, `end_time`)

---

### 11. **analytics-service**
**Назначение:** построение отчётных витрин и дашбордов.  
**MVP-сущности:**
- `fact_reservations` (`date`, `count`, `workspace_id`)
- `fact_payments` (`date`, `amount`, `method`)
- `dimension_date` (`date`, `day`, `month`, `year`)

---

### 12. **notification-service**
**Назначение:** отправка email/SMS/push уведомлений.  
**MVP-сущности:**
- `templates` (`id`, `name`, `channel`, `body`)
- `notifications` (`id`, `template_id`, `recipient`, `payload`, `status`)

---

### 13. **api-gateway / bff**
**Назначение:** единая точка входа, валидация JWT, агрегация API.  
**MVP-сущности:**  
_не требует собственных БД — проксирование и агрегация._

---

## Пример взаимодействия сервисов (MVP)

## Расширенный пример взаимодействия всех сервисов (MVP)

```mermaid
sequenceDiagram
    participant U as Пользователь
    participant G as API-Gateway/BFF
    participant A as auth-service
    participant H as hr-service
    participant W as workspace-service
    participant B as booking-service
    participant P as payment-service
    participant GA as guest-access-service
    participant PK as parking-service
    participant O as order-service
    participant S as support-service
    participant SC as security-service
    participant N as notification-service
    participant AN as analytics-service
    participant AC as accounting-service

    %% Аутентификация и получение профиля
    U->>A: POST /auth/login (email, password)
    A-->>U: { accessJWT, refreshToken }
    U->>G: GET /profile (Bearer accessJWT)
    G->>H: GET /employees/{userId}
    H-->>G: { role, shifts }
    G-->>U: { userProfile }

    %% Получение каталога ресурсов
    U->>G: GET /workspaces
    G->>W: GET /workspaces
    W-->>G: [workspaces]
    G-->>U: [workspaces]

    U->>G: GET /resources?status=free&type=desk
    G->>W: GET /seats?status=free&type=desk
    W-->>G: [seats]
    G-->>U: [seats]

    %% Бронирование
    U->>G: POST /reservations { resourceIds, start, end }
    G->>B: POST /reservations
    B-->>G: { reservationId }
    B->>AN: publish reservation.created
    B-->>G: 201 Created

    %% Обновление статуса ресурса
    AN-->>W: reservation.created
    W->>W: UPDATE seats SET status='occupied'
    W->>AN: publish seat.status_changed

    %% Платёж
    U->>G: POST /payments { reservationId, method }
    G->>P: POST /payments
    P-->>G: { paymentId, status: processing }
    P->>AC: create ledger_entry
    P->>N: notify payment.processing
    P->>AN: publish payment.created

    Note over P: эквайер callback
    P->>P: UPDATE payments SET status='succeeded'
    P->>N: notify payment.succeeded
    P->>AN: publish payment.succeeded
    AN-->>B: payment.succeeded
    B->>B: UPDATE reservations SET status='CONFIRMED'

    %% Гостевой доступ
    U->>G: POST /guest-passes { guestInfo, workspaceId, period }
    G->>GA: POST /guest-passes
    GA-->>G: { passId, qrCode }
    GA->>N: notify guest.pass_issued
    GA->>AN: publish guest_pass.created

    %% Парковка
    U->>G: POST /parking/orders { slotId, from, to }
    G->>PK: POST /parking/orders
    PK-->>G: { parkingOrderId }
    PK->>N: notify parking.reserved
    PK->>AN: publish parking.order.created

    %% Заказы доп. услуг
    U->>G: POST /orders { items }
    G->>O: POST /orders
    O-->>G: { orderId }
    O->>N: notify order.created
    O->>AN: publish order.created

    %% Техподдержка
    U->>G: POST /tickets { category, description }
    G->>S: POST /tickets
    S-->>G: { ticketId }
    S->>N: notify ticket.created
    S->>AN: publish ticket.created

    %% СКУД
    U->>G: POST /access/verify { passCode }
    G->>SC: POST /access/verify
    SC-->>G: { granted/denied }
    SC->>AN: publish access.event

    Note over AN,N:  
      • Analytics-svc собирает все события  
      • Notification-svc рассылает уведомления  

