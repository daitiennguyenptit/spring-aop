package com.example.aop.pitfalls.service;

import com.example.aop.pitfalls.annotation.Traced;
import org.springframework.stereotype.Service;

@Service
public class RefactoredInvoiceService {

    private final InvoiceValidator validator;

    public RefactoredInvoiceService(InvoiceValidator validator) {
        this.validator = validator;
    }

    @Traced
    public long issue(long amount) {
        validator.validate(amount);   // goi bean khac -> qua proxy
        return amount;
    }
}
