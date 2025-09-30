package com.swiftcart.order.events;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderConfirmedEvent {
    public static class Item {
        public String sku;
        public Integer qty;
        public BigDecimal unitPrice;
    }

    public String eventId;
    public String eventType = "OrderConfirmed";
    public String occurredAt = OffsetDateTime.now().toString();
    public String orderId;
    public String userId;
    public List<Item> items;
    public BigDecimal totalAmount;
    public String currency;
    public String paymentId;
}
