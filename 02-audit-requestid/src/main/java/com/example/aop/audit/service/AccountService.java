package com.example.aop.audit.service;

import com.example.aop.audit.annotation.Audited;
import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Business logic thuan: khong co tham so requestId, khong co dong log audit nao.
 * Du lieu demo la synthetic (khong phai du lieu that).
 */
@Service
public class AccountService {

    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>(Map.of(
            "ACC-001", new BigDecimal("1000.00"),
            "ACC-002", new BigDecimal("500.00")));

    @Audited(action = "VIEW_BALANCE", logResult = true)
    public BigDecimal getBalance(String accountId) {
        BigDecimal balance = balances.get(accountId);
        if (balance == null) {
            throw new NoSuchElementException("Account not found: " + accountId);
        }
        return balance;
    }

    @Audited(action = "DEBIT")
    public void debit(String accountId, BigDecimal amount) {
        balances.compute(accountId, (id, current) -> {
            if (current == null) {
                throw new NoSuchElementException("Account not found: " + id);
            }
            if (current.compareTo(amount) < 0) {
                throw new InsufficientBalanceException(id);
            }
            return current.subtract(amount);
        });
    }

    @Audited(action = "CREDIT")
    public void credit(String accountId, BigDecimal amount) {
        balances.compute(accountId, (id, current) -> {
            if (current == null) {
                throw new NoSuchElementException("Account not found: " + id);
            }
            return current.add(amount);
        });
    }
}
