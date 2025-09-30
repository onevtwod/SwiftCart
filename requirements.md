## SwiftCart Requirements

### Functional Requirements
- Users can place an order with one or more products
- Real-time inventory validation before order confirmation
- Synchronous payment processing through third-party provider
- Update inventory immediately after confirmation
- Send confirmation email upon successful order

### Non-Functional Requirements
- High throughput (1000s orders/minute) with p99 < 200ms for order creation
- Reliability and durability: orders must never be lost
- Resilience: accept new orders even if non-critical services are down
- Scalability: independently scalable services
- Observability: metrics, logs, tracing for all services
- Security: authn/authz at gateway, encryption in transit/at rest

### Performance Targets
- p99 create-order latency < 200ms
- Inventory update median < 1s after confirmation
- End-to-end success rate SLO ≥ 99.9%

### Availability Targets
- Order API availability SLO ≥ 99.95%
- Degraded mode acceptable when notification is down

### Reliability & Consistency
- Event-driven with Kafka for durable, ordered events
- Eventual consistency across services via Saga (choreography)
- Idempotency for producers/consumers and external payment calls

### Security & Compliance
- OAuth2.1/OIDC via API Gateway; mTLS intra-cluster (as feasible)
- Least-privilege service accounts; database-per-service
- Secrets via Kubernetes Secrets/External Secrets

### Operability
- Docker + Kubernetes; HPA and PodDisruptionBudgets
- Prometheus, Loki/ELK, Jaeger/Tempo, Grafana
- Infrastructure as Code with Terraform


