package com.example.aop.pitfalls.service;

import com.example.aop.pitfalls.annotation.Traced;
import org.springframework.stereotype.Component;

/** CACH SUA 3 (khuyen nghi): tach logic can intercept ra bean rieng. */
@Component
public class InvoiceValidator {

    @Traced
    public void validate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
