## SwiftCart UI Guidelines and Screens

### Principles
- Prefer beautiful, modern components with accessible defaults and strong contrast
- Use icons (not emojis) for status, actions, and navigation [[memory:6230034]]
- Responsive layouts; minimal cognitive load; clear affordances
- Avoid yellow text; use a neutral or brand palette with sufficient contrast [[memory:7151025]]

### Admin Screens (internal)
- **Orders Dashboard**: list, filter, view details, trace IDs, statuses
- **Inventory Management**: product list, stock levels, adjustments, alerts
- **Kafka Monitoring (links)**: quick links to Grafana/Jaeger dashboards

### User-Facing (reference)
- **Checkout**: cart review, address/payment, order confirmation
- **Order Status**: show PENDING/CONFIRMED/FAILED with clear state, trace ID

### Components
- Buttons, inputs, tables with skeleton loading
- Toasts/snackbars for non-blocking feedback
- Status badges: pending, confirmed, failed
- Icons from a consistent pack (e.g., Lucide, Material, Heroicons)

### UX Notes
- Optimistic UI for order submission with idempotency key display
- Show order reference, total, and ETA immediately on confirmation
- Provide retry guidance on failure without exposing internals


