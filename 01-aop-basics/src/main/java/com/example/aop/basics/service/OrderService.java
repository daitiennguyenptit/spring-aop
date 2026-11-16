package com.example.aop.basics.service;

import org.springframework.stereotype.Service;

/**
 * Target object: chi chua business logic, KHONG biet gi ve logging/tracing.
 */
@Service
public class OrderService {

    public String placeOrder(String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        return "ORDER-" + sku + "-x" + quantity;
    }

    public int countOrders() {
        return 42;
    }
}
