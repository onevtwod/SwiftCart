package com.swiftcart.order.service;

import com.swiftcart.order.domain.IdempotencyRecord;
import com.swiftcart.order.domain.OrderEntity;
import com.swiftcart.order.domain.OrderItemEntity;
import com.swiftcart.order.events.KafkaOrderPublisher;
import com.swiftcart.order.events.OrderConfirmedEvent;
import com.swiftcart.order.events.OrderFailedEvent;
import com.swiftcart.order.payment.PaymentClient;
import com.swiftcart.order.payment.PaymentResponse;
import com.swiftcart.order.repo.IdempotencyRecordRepository;
import com.swiftcart.order.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderCreationService {

    private final OrderRepository orderRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final PaymentClient paymentClient;
    private final KafkaOrderPublisher publisher;

    public OrderCreationService(OrderRepository orderRepository,
                                IdempotencyRecordRepository idempotencyRecordRepository,
                                PaymentClient paymentClient,
                                KafkaOrderPublisher publisher) {
        this.orderRepository = orderRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.paymentClient = paymentClient;
        this.publisher = publisher;
    }

    @Transactional
    public OrderEntity createOrderIdempotent(String idempotencyKey, Map<String, Object> request) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey)
                    .map(rec -> orderRepository.findById(rec.getOrderId()).orElseThrow())
                    .orElseGet(() -> createAndRecord(idempotencyKey, request));
        }
        return createNew(request);
    }

    private OrderEntity createAndRecord(String idempotencyKey, Map<String, Object> request) {
        OrderEntity order = createNew(request);
        IdempotencyRecord rec = new IdempotencyRecord();
        rec.setIdempotencyKey(idempotencyKey);
        rec.setOrderId(order.getId());
        idempotencyRecordRepository.save(rec);
        return order;
    }

    private OrderEntity createNew(Map<String, Object> request) {
        OrderEntity order = new OrderEntity();
        String userId = (String) request.getOrDefault("userId", "anonymous");
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setCurrency((String) request.getOrDefault("currency", "USD"));

        BigDecimal total = BigDecimal.ZERO;
        List<OrderConfirmedEvent.Item> eventItems = new ArrayList<>();
        Object itemsObj = request.get("items");
        if (itemsObj instanceof List<?> items) {
            for (Object o : items) {
                if (o instanceof Map<?, ?> m) {
                    OrderItemEntity item = new OrderItemEntity();
                    item.setSku((String) m.get("sku"));
                    Integer qty = Integer.valueOf(String.valueOf(m.getOrDefault("qty", 1)));
                    BigDecimal unit = new BigDecimal(String.valueOf(m.getOrDefault("unitPrice", "0")));
                    item.setQty(qty);
                    item.setUnitPrice(unit);
                    order.addItem(item);

                    total = total.add(unit.multiply(BigDecimal.valueOf(qty)));

                    OrderConfirmedEvent.Item ei = new OrderConfirmedEvent.Item();
                    ei.sku = item.getSku();
                    ei.qty = qty;
                    ei.unitPrice = unit;
                    eventItems.add(ei);
                }
            }
        }
        order.setTotalAmount(total);

        PaymentResponse payment = paymentClient.charge(userId, total, order.getCurrency());
        if (!payment.isSucceeded()) {
            order.setStatus("FAILED");
            OrderEntity savedFailed = orderRepository.save(order);
            OrderFailedEvent fe = new OrderFailedEvent();
            fe.eventId = UUID.randomUUID().toString();
            fe.orderId = savedFailed.getId();
            fe.userId = userId;
            fe.totalAmount = total;
            fe.currency = savedFailed.getCurrency();
            fe.reason = payment.getMessage();
            publisher.publishOrderFailed(fe);
            return savedFailed;
        }

        order.setStatus("CONFIRMED");
        OrderEntity saved = orderRepository.save(order);

        OrderConfirmedEvent event = new OrderConfirmedEvent();
        event.eventId = UUID.randomUUID().toString();
        event.orderId = saved.getId();
        event.userId = userId;
        event.items = eventItems;
        event.totalAmount = total;
        event.currency = saved.getCurrency();
        event.paymentId = payment.getPaymentId();
        publisher.publishOrderConfirmed(event);

        return saved;
    }
}
