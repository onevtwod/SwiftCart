## SwiftCart Tasks (Checklist)

### Foundations
- [x] Initialize mono-repo or multi-repo structure
- [x] Define service scaffolds (Gateway, Order, Payment, Inventory, Notification)
- [x] Set up Dockerfiles and docker-compose for local
- [x] Configure Kubernetes manifests (dev/stage/prod overlays)
- [x] Provision Kafka (local + cloud), topics, and DLQs (local+cloud scripts)

### Order Service
- [x] Create `POST /orders` and `GET /orders/{id}`
- [x] Implement idempotency keys for order creation
- [x] Integrate Payment service with Resilience4j (timeouts, circuit breaker, retries)
- [x] Persist orders in PostgreSQL (orders, order_items)
- [x] Publish `OrderConfirmedEvent` and `OrderFailedEvent` (consider Outbox)
- [x] Tracing, metrics, structured logging

### Payment Service
- [x] Implement provider client (Stripe SDK or adapter) (stubbed)
- [x] Idempotent charge API; map provider errors (basic)
- [x] Expose health/readiness endpoints (Actuator)
- [x] Tracing, metrics, structured logging

### Inventory Service
- [x] Model products and inventory movements (PostgreSQL)
- [x] Redis cache for hot stock counters
- [x] Consume `orders.confirmed.v1`; decrement stock idempotently (skeleton consumer)
- [x] Backfill/reconciliation job from order store
- [x] Tracing, metrics, structured logging

### Notification Service
- [x] Consume `orders.confirmed.v1`
- [x] Integrate SMTP/API provider; template emails (stub via Mailhog)
- [x] Retry with DLQ on failures
- [x] Tracing, metrics, structured logging

### API Gateway
- [x] Configure routes, rate limits, auth (OIDC)
- [x] Request/response logging and correlation IDs

### Observability & Ops
- [x] Prometheus, Grafana dashboards, RED/USE (compose + scrape config)
- [x] Centralized logs (Loki/ELK) with retention
- [x] Jaeger/Tempo tracing and sampling (Zipkin added for tracing collection)
- [x] Terraform for infra provisioning (AKS skeleton added)
- [x] CI/CD pipelines with canary/blue-green (initial CD on tags)

### Security
- [x] mTLS service mesh or internal TLS (Istio PeerAuthentication in dev)
- [x] Secret management and rotation (secrets wired)
- [x] AuthZ policies (least privilege) (gateway scopes)

### Testing & QA
- [x] Unit and contract tests (Pact) for services (initial)
- [x] Load tests (k6/Gatling) to validate p99 latencies (script added)
- [x] Chaos tests for resilience (pod kills, broker outages) (script added)


