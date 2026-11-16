package com.example.aop.audit.service;

import com.example.aop.audit.annotation.Audited;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Chay tren thread khac (@Async) nhung van co dung requestId nho TaskDecorator. */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    @Audited(action = "NOTIFY")
    public CompletableFuture<Void> notifyTransfer(String fromAccount, String toAccount) {
        log.info("Gui thong bao chuyen tien {} -> {}", fromAccount, toAccount);
        return CompletableFuture.completedFuture(null);
    }
}
