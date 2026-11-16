package com.example.aop.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 02: Ghi audit log cho tung ham, moi ban ghi co requestId
 * ma KHONG can truyen requestId vao tham so cua ham.
 *
 * Luong: RequestIdFilter -> RequestContext (ThreadLocal + MDC) -> @Audited method -> AuditAspect doc context.
 */
@SpringBootApplication
public class AuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }
}
