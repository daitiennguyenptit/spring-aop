package com.example.aop.pitfalls.service;

import com.example.aop.pitfalls.annotation.Traced;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/** CACH SUA 1: tu inject chinh minh (qua proxy). @Lazy de tranh circular reference khi khoi tao. */
@Service
public class SelfInjectedInvoiceService {

    private final SelfInjectedInvoiceService self;

    public SelfInjectedInvoiceService(@Lazy SelfInjectedInvoiceService self) {
        this.self = self;
    }

    @Traced
    public long issue(long amount) {
        self.validate(amount);   // goi qua proxy -> advice chay
        return amount;
    }

    @Traced
    public void validate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
