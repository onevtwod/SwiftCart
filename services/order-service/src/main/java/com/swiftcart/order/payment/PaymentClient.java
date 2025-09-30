package com.swiftcart.order.payment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PaymentClient {

    private final WebClient webClient;
    private final String baseUrl;

    public PaymentClient(WebClient.Builder builder,
            @Value("${payment.baseUrl}") String baseUrl) {
        this.webClient = builder.build();
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "payment", fallbackMethod = "fallbackCharge")
    public PaymentResponse charge(String userId, BigDecimal amount, String currency) {
        Map<String, Object> payload = Map.of(
                "userId", userId,
                "amount", amount,
                "currency", currency);
        return webClient.post()
                .uri(baseUrl + "/payments/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    private PaymentResponse fallbackCharge(String userId, BigDecimal amount, String currency, Throwable t) {
        return new PaymentResponse("failed", null, t.getMessage());
    }
}
