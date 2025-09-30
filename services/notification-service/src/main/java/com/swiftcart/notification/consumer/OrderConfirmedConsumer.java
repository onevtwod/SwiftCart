package com.swiftcart.notification.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class OrderConfirmedConsumer {

    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public OrderConfirmedConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "${kafka.topics.ordersConfirmed}", groupId = "notification-service")
    public void onOrderConfirmed(ConsumerRecord<String, Map<String, Object>> record) {
        Map<String, Object> event = record.value();
        if (random.nextInt(10) == 0) { // 10% simulated transient failure
            throw new RuntimeException("Transient mail send failure");
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("customer@example.com");
        msg.setSubject("Your order is confirmed");
        msg.setText("Order " + event.get("orderId") + " confirmed. Total: " + event.get("totalAmount"));
        mailSender.send(msg);
    }

    @KafkaListener(topics = "${kafka.topics.ordersConfirmedDlq}", groupId = "notification-service-dlq")
    public void onOrderConfirmedDlq(ConsumerRecord<String, Map<String, Object>> record) {
        Map<String, Object> event = record.value();
        // TODO: persist for manual review or trigger compensations
    }
}
