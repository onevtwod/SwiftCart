package com.swiftcart.order.events;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderFailedEvent {
    public String eventId;
    public String eventType = "OrderFailed";
    public String occurredAt = OffsetDateTime.now().toString();
    public String orderId;
    public String userId;
    public BigDecimal totalAmount;
    public String currency;
    public String reason;
}
