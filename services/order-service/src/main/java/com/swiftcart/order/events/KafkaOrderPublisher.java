package com.swiftcart.order.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String confirmedTopic;
    private final String failedTopic;

    public KafkaOrderPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${kafka.topics.ordersConfirmed}") String confirmedTopic,
                               @Value("${kafka.topics.ordersFailed:orders.failed.v1}") String failedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.confirmedTopic = confirmedTopic;
        this.failedTopic = failedTopic;
    }

    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        kafkaTemplate.send(confirmedTopic, event.orderId, event);
    }

    public void publishOrderFailed(OrderFailedEvent event) {
        kafkaTemplate.send(failedTopic, event.orderId, event);
    }
}
