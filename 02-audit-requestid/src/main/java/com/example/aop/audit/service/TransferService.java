package com.example.aop.audit.service;

import com.example.aop.audit.annotation.Audited;
import com.example.aop.audit.annotation.Sensitive;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Mot request -> nhieu method long nhau (transfer -> debit -> credit -> notify async).
 * Tat ca ban ghi audit deu chung mot requestId ma khong method nao nhan requestId.
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountService accountService;
    private final NotificationService notificationService;

    public TransferService(AccountService accountService, NotificationService notificationService) {
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @Audited(action = "TRANSFER", logResult = true)
    public String transfer(String fromAccount, String toAccount, BigDecimal amount, @Sensitive String otp) {
        log.info("Bat dau chuyen tien");   // dong log nay cung tu co requestId nho MDC
        accountService.debit(fromAccount, amount);
        accountService.credit(toAccount, amount);
        notificationService.notifyTransfer(fromAccount, toAccount);
        return "TRANSFERRED " + amount + " " + fromAccount + "->" + toAccount;
    }
}
