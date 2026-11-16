package com.example.aop.retry.web;

import com.example.aop.retry.service.PaymentGatewayClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentGatewayClient client;

    public PaymentController(PaymentGatewayClient client) {
        this.client = client;
    }

    @PostMapping("/payments/{paymentId}")
    public String charge(@PathVariable String paymentId) {
        return client.charge(paymentId);
    }
}
