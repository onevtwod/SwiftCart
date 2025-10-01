package com.swiftcart.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.Map;

@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        ExponentialBackOff backoff = new ExponentialBackOff();
        backoff.setInitialInterval(200L);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(2000L);
        backoff.setMaxElapsedTime(10000L); // 10 seconds max
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backoff);
        handler.setClassifications(Map.of(Exception.class, true), false);
        return handler;
    }
}
