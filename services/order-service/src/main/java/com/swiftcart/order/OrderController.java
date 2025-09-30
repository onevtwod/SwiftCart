package com.swiftcart.order;

import com.swiftcart.order.domain.OrderEntity;
import com.swiftcart.order.service.OrderCreationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderCreationService orderCreationService;

    public OrderController(OrderCreationService orderCreationService) {
        this.orderCreationService = orderCreationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody Map<String, Object> body) {
        OrderEntity order = orderCreationService.createOrderIdempotent(idempotencyKey, body);
        return ResponseEntity.status(201).body(Map.of(
                "orderId", order.getId(),
                "status", order.getStatus(),
                "totalAmount", order.getTotalAmount(),
                "currency", order.getCurrency()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable("id") String id) {
        return ResponseEntity.ok(Map.of(
                "orderId", id,
                "status", "PENDING"));
    }
}
