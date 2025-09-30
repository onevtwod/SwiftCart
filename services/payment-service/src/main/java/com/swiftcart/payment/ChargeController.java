package com.swiftcart.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/payments")
public class ChargeController {

    private final ConcurrentHashMap<String, Map<String, Object>> idempotencyStore = new ConcurrentHashMap<>();

    @PostMapping("/charge")
    public ResponseEntity<Map<String, Object>> charge(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody Map<String, Object> body
    ) {
        String key = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        Map<String, Object> response = idempotencyStore.computeIfAbsent(key, k -> Map.of(
                "status", "succeeded",
                "paymentId", "pi_" + UUID.randomUUID()
        ));
        return ResponseEntity.ok(response);
    }
}
