package com.showcase.booking.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PaymentGatewayClient {
    private final double randomFailureRate;

    public PaymentGatewayClient(@Value("${simulation.random-payment-failure-rate:0.18}") double randomFailureRate) {
        this.randomFailureRate = randomFailureRate;
    }

    @Retry(name = "payment")
    @CircuitBreaker(name = "payment", fallbackMethod = "fallbackCharge")
    public String charge(String customerEmail, int amountCents, boolean forceFailure) {
        if (forceFailure || ThreadLocalRandom.current().nextDouble() < randomFailureRate) {
            throw new IllegalStateException("Payment gateway timeout");
        }
        return "pay_" + UUID.randomUUID();
    }

    @SuppressWarnings("unused")
    String fallbackCharge(String customerEmail, int amountCents, boolean forceFailure, Throwable throwable) {
        throw new IllegalStateException("Payment service unavailable after retries: " + throwable.getMessage());
    }
}
