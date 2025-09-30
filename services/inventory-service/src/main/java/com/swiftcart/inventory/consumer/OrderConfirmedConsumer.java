package com.swiftcart.inventory.consumer;

import com.swiftcart.inventory.service.StockService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderConfirmedConsumer {

    private final StockService stockService;

    public OrderConfirmedConsumer(StockService stockService) {
        this.stockService = stockService;
    }

    @KafkaListener(topics = "${kafka.topics.ordersConfirmed}", groupId = "inventory-service")
    public void onOrderConfirmed(ConsumerRecord<String, Map<String, Object>> record) {
        Map<String, Object> event = record.value();
        Object itemsObj = event.get("items");
        if (itemsObj instanceof List<?> items) {
            for (Object o : items) {
                if (o instanceof Map<?, ?> m) {
                    String sku = String.valueOf(m.get("sku"));
                    int qty = Integer.parseInt(String.valueOf(m.get("qty")));
                    stockService.decrement(sku, qty);
                }
            }
        }
    }
}
