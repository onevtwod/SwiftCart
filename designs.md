## SwiftCart System Design

### 1. Overview
SwiftCart is a cloud-native, event-driven microservices backend for real-time order processing and inventory management. It targets very high throughput with low latency, strong durability, and resilience against downstream failures.

### 2. High-Level Architecture
- **API Gateway (Edge)**: Routes, rate-limits, authenticates.
- **Order Service**: Validates requests, calls Payment synchronously with circuit breaker, persists orders, emits events.
- **Payment Service**: Integrates with external PSP (e.g., Stripe). Idempotent, secured, observable.
- **Inventory Service**: Updates stock on events; uses Redis for hot counters and PostgreSQL for durability.
- **Notification Service**: Sends emails asynchronously; resilient to outages via Kafka offsets/retries.
- **Kafka**: Durable event backbone decoupling synchronous order-taking from asynchronous processing.
- **Datastores**: PostgreSQL per service (ownership enforced), Redis for hot-path counters.

Flow (happy path):
1) Client → `POST /orders` at Gateway → Order Service
2) Order Service → Payment Service (sync, circuit breaker)
3) Persist Order = CONFIRMED → Publish `OrderConfirmedEvent`
4) Inventory and Notification consume event asynchronously

### 3. Core Design Patterns
- **Asynchronous Messaging**: Kafka topics deliver durable, ordered events; downstream services process independently.
- **Saga (Choreography)**: Distributed transaction via domain events. Each service reacts to prior service events and emits compensations on failure.
- **Circuit Breaker & Timeouts**: Resilience4j guards synchronous calls to Payment; prevents cascading failures.
- **Idempotency**: Order creation keys, payment request ids, and consumer processing ensure safe retries.
- **Outbox Pattern (optional)**: For atomic write of order + event publish using a local outbox table and background relay.

### 4. Services and Storage
- **Order Service (Spring Boot, Java)**
  - API: Create order, get order
  - DB: PostgreSQL (orders, order_items)
  - Emits: `OrderConfirmedEvent`, `OrderFailedEvent`
- **Payment Service (Spring Boot, Java)**
  - External: Stripe (or mock in lower envs)
  - Emits: `PaymentSucceededEvent`, `PaymentFailedEvent` (optional if orchestrating)
- **Inventory Service (Spring Boot, Java)**
  - DB: PostgreSQL (products, inventory_movements)
  - Cache: Redis (available_stock:<sku>)
  - Consumes: `OrderConfirmedEvent`
- **Notification Service (Spring Boot, Java)**
  - Consumes: `OrderConfirmedEvent`
  - Email provider via SMTP/API
- **API Gateway (Spring Cloud Gateway)**
  - Routing, rate limits, authz/authn

### 5. Data Flow and Topics
Topics (Kafka):
- `orders.confirmed.v1`
- `orders.failed.v1`
- (optional) `payments.succeeded.v1`, `payments.failed.v1`

Event (example):
```
{
  "eventId": "uuid",
  "eventType": "OrderConfirmed",
  "occurredAt": "2025-09-30T12:34:56Z",
  "order": {
    "orderId": "uuid",
    "userId": "uuid",
    "items": [{"sku": "SKU-123", "qty": 2, "unitPrice": 1299}],
    "totalAmount": 2598,
    "currency": "USD"
  },
  "payment": {
    "provider": "stripe",
    "paymentId": "pi_123",
    "status": "succeeded"
  }
}
```

### 6. Consistency and Reliability
- **Database-per-Service**: Enforces loose coupling; no cross-service DB access.
- **Eventual Consistency**: Inventory lags by sub-second; acceptable for throughput/resilience gains.
- **Durability**: Kafka replication (RF≥3), acks=all, idempotent producers; PostgreSQL with WAL, regular backups.
- **Exactly-Once Semantics (practical)**: Achieved via idempotent consumers and deduplication keys.

### 7. Resilience and Backpressure
- **Circuit Breakers**: On Payment; fast-fail under slowness.
- **Retries with Jitter**: Producer and consumer retry policies with exponential backoff and DLQs.
- **Dead Letter Queues**: Per-topic DLQ (e.g., `orders.confirmed.dlq`) for poison messages.
- **Bulkheads & Timeouts**: Isolate resource pools; enforce strict timeouts.
- **Backpressure**: Kafka consumer max.poll settings, rate limits at Gateway, adaptive concurrency.

### 8. Performance Targets
- p99 create-order latency < 200ms (excluding email send)
- 1000s of orders/minute sustained
- Inventory updates < 1s median from event publish

### 9. Observability
- **Metrics**: Prometheus scraping JVM, Kafka, PostgreSQL; SLOs on latency/error rate.
- **Logs**: Structured JSON; centralized via Loki/ELK.
- **Tracing**: Jaeger/Tempo; propagate W3C tracecontext across services and Kafka.
- **Dashboards**: Grafana single pane with RED/USE views.

### 10. Security & Compliance
- OAuth 2.1/OIDC at Gateway, mTLS service-to-service (env-dependent)
- Secrets via Kubernetes Secrets/External Secrets; never in images
- PII minimization and encryption at rest/in transit

### 11. Deployment & Operations
- Docker images per service; distroless base where possible
- Kubernetes with HPA (CPU/RPS), PodDisruptionBudgets, anti-affinity
- Blue/green or canary rollouts; automated migrations
- Terraform for infra; env parity across dev/stage/prod

### 12. API Sketch
- `POST /orders` → 201 with orderId; idempotency-key header supported
- `GET /orders/{id}` → 200 with state (PENDING|CONFIRMED|FAILED)

### 13. Risks & Mitigations
- Payment outage → Circuit breaker + user-visible failure; no partial orders
- Inventory race conditions → Atomic reservation strategy + idempotent decrements
- Poison events → DLQ with alerting, replay tooling

### 14. Future Enhancements
- Outbox pattern adoption for all event producers
- CQRS read models for order dashboards
- Multi-region active-active with geo-replicated Kafka


