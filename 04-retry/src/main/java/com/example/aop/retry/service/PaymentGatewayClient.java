package com.example.aop.retry.service;

import com.example.aop.retry.annotation.RetryOnFailure;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * Gia lap client goi cong thanh toan: 2 lan dau timeout, lan 3 thanh cong.
 * paymentId dong vai tro idempotency key nen retry an toan.
 */
@Service
public class PaymentGatewayClient {

    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private volatile int failuresBeforeSuccess = 2;

    @RetryOnFailure(maxAttempts = 3, backoffMs = 50, retryFor = TransientException.class)
    public String charge(String paymentId) {
        if (paymentId.startsWith("BLOCKED")) {
            attempts.computeIfAbsent(paymentId, k -> new AtomicInteger()).incrementAndGet();
            throw new BusinessException("Card blocked");
        }
        int attempt = attempts.computeIfAbsent(paymentId, k -> new AtomicInteger()).incrementAndGet();
        if (attempt <= failuresBeforeSuccess) {
            throw new TransientException("Gateway timeout (attempt " + attempt + ")");
        }
        return "CHARGED " + paymentId + " after " + attempt + " attempt(s)";
    }

    public int attemptsOf(String paymentId) {
        AtomicInteger counter = attempts.get(paymentId);
        return counter == null ? 0 : counter.get();
    }

    public void setFailuresBeforeSuccess(int failuresBeforeSuccess) {
        this.failuresBeforeSuccess = failuresBeforeSuccess;
    }
}
