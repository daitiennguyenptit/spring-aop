package com.example.aop.audit.web;

import com.example.aop.audit.service.AccountService;
import com.example.aop.audit.service.TransferService;
import com.example.aop.audit.store.AuditLogStore;
import com.example.aop.audit.store.AuditRecord;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BankController {

    private final TransferService transferService;
    private final AccountService accountService;
    private final AuditLogStore auditLogStore;

    public BankController(TransferService transferService, AccountService accountService, AuditLogStore auditLogStore) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.auditLogStore = auditLogStore;
    }

    public record TransferRequest(String from, String to, BigDecimal amount, String otp) {
    }

    @PostMapping("/transfers")
    public String transfer(@RequestBody TransferRequest request) {
        return transferService.transfer(request.from(), request.to(), request.amount(), request.otp());
    }

    @GetMapping("/accounts/{id}/balance")
    public BigDecimal balance(@PathVariable String id) {
        return accountService.getBalance(id);
    }

    /** Endpoint demo de xem audit log. Thuc te phai phan quyen chat che. */
    @GetMapping("/audit-logs")
    public List<AuditRecord> auditLogs(@RequestParam(required = false) String requestId) {
        return requestId == null ? auditLogStore.findAll() : auditLogStore.findByRequestId(requestId);
    }
}
