package com.example.aop.pitfalls.service;

import com.example.aop.pitfalls.annotation.Traced;
import org.springframework.stereotype.Service;

/** Chua cac loi kinh dien khien advice KHONG chay. */
@Service
public class BuggyInvoiceService {

    private final TaxCalculator taxCalculator;

    public BuggyInvoiceService(TaxCalculator taxCalculator) {
        this.taxCalculator = taxCalculator;
    }

    /** PITFALL 1 - self-invocation: this.validate() goi thang vao target, KHONG qua proxy. */
    @Traced
    public long issue(long amount) {
        validate(amount);
        return amount + taxCalculator.tax(amount);
    }

    @Traced
    public void validate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }

    /**
     * PITFALL 2 - final method: CGLIB khong override duoc -> advice khong chay,
     * va method chay tren chinh object proxy (field chua duoc inject -> null!).
     */
    @Traced
    public final String finalMethod() {
        return taxCalculator == null ? "taxCalculator is NULL (running on proxy instance)" : "ok";
    }
}
