package com.swiftcart.order.service;

import com.swiftcart.order.domain.OrderEntity;
import com.swiftcart.order.events.KafkaOrderPublisher;
import com.swiftcart.order.payment.PaymentClient;
import com.swiftcart.order.payment.PaymentResponse;
import com.swiftcart.order.repo.IdempotencyRecordRepository;
import com.swiftcart.order.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class OrderCreationServiceTest {

    @Test
    void createOrder_succeeds_andPublishesEvent() {
        OrderRepository orderRepo = Mockito.mock(OrderRepository.class);
        IdempotencyRecordRepository idemRepo = Mockito.mock(IdempotencyRecordRepository.class);
        PaymentClient paymentClient = Mockito.mock(PaymentClient.class);
        KafkaOrderPublisher publisher = Mockito.mock(KafkaOrderPublisher.class);

        Mockito.when(paymentClient.charge(anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(new PaymentResponse("succeeded", "pi_test", null));
        Mockito.when(orderRepo.save(any(OrderEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        OrderCreationService svc = new OrderCreationService(orderRepo, idemRepo, paymentClient, publisher);

        Map<String, Object> req = Map.of(
                "userId", "u1",
                "currency", "USD",
                "items", List.of(Map.of("sku", "SKU-1", "qty", 1, "unitPrice", 10)));

        OrderEntity order = svc.createOrderIdempotent(null, req);
        assertNotNull(order.getId());
        assertEquals("CONFIRMED", order.getStatus());
        assertEquals(new BigDecimal("10"), order.getTotalAmount());
        Mockito.verify(publisher).publishOrderConfirmed(any());
    }
}
