package com.example.aop.pitfalls.service;

import com.example.aop.pitfalls.annotation.Traced;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/** CACH SUA 2: AopContext.currentProxy() - can @EnableAspectJAutoProxy(exposeProxy = true). Code bi phu thuoc Spring AOP. */
@Service
public class AopContextInvoiceService {

    @Traced
    public long issue(long amount) {
        ((AopContextInvoiceService) AopContext.currentProxy()).validate(amount);
        return amount;
    }

    @Traced
    public void validate(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
    }
}
