## Пример взаимодействия сервисов (MVP)

## Расширенный пример взаимодействия всех сервисов (MVP)

```mermaid
sequenceDiagram
    autonumber
    participant Client as Web/Mobile App
    participant API as API-Gateway/BFF
    participant AUTH as auth-service
    participant WS as workspace-service
    participant BK as booking-service
    participant PAY as payment-service
    participant GA as guest-access-service
    participant PK as parking-service
    participant ORD as order-service
    participant SUP as support-service
    participant SEC as security-service
    participant HR as hr-service
    participant AN as analytics-service
    participant NOT as notification-service

    %% 1. Аутентификация
    Client->>API: POST /auth/login {email, password}
    API->>AUTH: POST /login
    AUTH-->>API: {accessToken, refreshToken}
    API-->>Client: 200 {accessToken}

    %% 2. Получение списка рабочих мест
    Client->>API: GET /api/workspaces
    API->>WS: GET /workspaces
    WS-->>API: [ workspaces… ]
    API-->>Client: 200 [ workspaces… ]

    %% 3. Бронирование места + выставление счета
    Client->>API: POST /api/reservations {resourceId, start, end}
    API->>BK: POST /reservations
    BK->>WS: GET /resources/{id}        note right of WS: check availability
    WS-->>BK: {resource}
    BK-->>BK: create reservation & items
    BK->>PAY: POST /invoices {reservationId, amount}
    PAY-->>BK: {invoiceId}
    BK-->>API: {reservationId, invoiceId}
    API-->>Client: 201 {reservationId, invoiceId}

    %% 4. Оплата
    Client->>API: POST /api/payments {invoiceId, paymentMethodId}
    API->>PAY: POST /payments
    PAY-->>PAY: process with provider
    PAY-->>AN: InvoicePaidEvent
    PAY-->>NOT: InvoicePaidEvent
    PAY-->>BK: InvoicePaidEvent
    PAY-->>Client: 200 {paymentId, status}

    %% 5. Подтверждение брони
    BK->>BK: mark reservation confirmed
    BK-->>AN: ReservationConfirmedEvent
    BK-->>NOT: ReservationConfirmedEvent

    %% 6. Гостевой доступ
    Client->>API: POST /api/guest-passes {hostUserId, guestName, start, end}
    API->>GA: POST /guest-passes
    GA-->>NOT: GuestPassRequestedEvent
    GA-->>API: {guestPassId, qrToken}
    API-->>Client: 201 {guestPassId, qrToken}

    %% 7. Проход гостя через турникет
    SEC->>GA: POST /visit-logs {guestPassId, gateId, action: enter}
    GA-->>GA: create visit_log
    GA-->>AN: VisitLoggedEvent

    %% 8. Парковка
    Client->>API: POST /api/parking-reservations {vehicleId, spotId, start, end}
    API->>PK: POST /parking-reservations
    PK->>PAY: POST /invoices {reservationId, amount}
    PAY-->>PK: {invoiceId}
    PK-->>API: {reservationId, invoiceId}
    API-->>Client: 201 {reservationId, invoiceId}

    %% 9. Заказ канцелярии/еды
    Client->>API: POST /api/orders {items[], workspaceId}
    API->>ORD: POST /orders
    ORD-->>ORD: reserve stock
    ORD->>PAY: POST /invoices {orderId, amount}
    PAY-->>ORD: {invoiceId}
    ORD-->>API: {orderId, invoiceId}
    API-->>Client: 201 {orderId, invoiceId}
    ORD-->>AN: OrderCreatedEvent
    ORD-->>NOT: OrderCreatedEvent

    %% 10. Тикеты поддержки
    Client->>API: POST /api/tickets {subject, description}
    API->>SUP: POST /tickets
    SUP-->>SUP: create ticket
    SUP-->>NOT: TicketOpenedEvent
    SUP-->>API: {ticketId}
    API-->>Client: 201 {ticketId}

    %% 11. СКУД — события доступа
    SEC->>SEC: collect events from IoT gates
    SEC-->>NOT: AccessEvent
    SEC-->>AN: AccessEvent

    %% 12. HR — смены сотрудников
    HR-->>AN: ShiftScheduledEvent

    %% 13. Аналитика и уведомления
    AN-->>AN: aggregate metrics into facts
    NOT-->>NOT: send email/push/SMS on subscribed events

    %% 14. Общая агрегация
    Client->>API: GET /api/dashboard
    API->>BK: GET /reservations?userId
    API->>PAY: GET /invoices?userId
    API->>NOT: GET /notifications?userId
    API-->>Client: 200 {reservations, invoices, notifications}

