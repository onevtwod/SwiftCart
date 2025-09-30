package com.swiftcart.order.payment;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "payment-provider", port = "0")
class PaymentClientPactTest {

    @au.com.dius.pact.consumer.junit5.Pact(consumer = "order-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a payment charge request")
                .path("/payments/charge")
                .method("POST")
                .willRespondWith()
                .status(200)
                .body("{\"status\":\"succeeded\",\"paymentId\":\"pi_123\"}")
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void paymentClient_charges_successfully(MockServer mockServer) {
        PaymentClient client = new PaymentClient(WebClient.builder(), mockServer.getUrl());
        PaymentResponse resp = client.charge("u1", new BigDecimal("10"), "USD");
        assertEquals("succeeded", resp.getStatus());
        assertEquals("pi_123", resp.getPaymentId());
    }
}
