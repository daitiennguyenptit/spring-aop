package com.example.aop.audit.service;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountId) {
        super("Insufficient balance on account " + accountId);
    }
}
