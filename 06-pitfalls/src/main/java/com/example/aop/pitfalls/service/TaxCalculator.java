package com.example.aop.pitfalls.service;

import org.springframework.stereotype.Component;

@Component
public class TaxCalculator {
    public long tax(long amount) {
        return amount / 10;
    }
}
