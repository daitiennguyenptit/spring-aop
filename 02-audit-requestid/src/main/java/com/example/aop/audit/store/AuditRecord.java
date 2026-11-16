package com.example.aop.audit.store;

import java.time.Instant;

/** Mot ban ghi audit. requestId duoc lay tu RequestContext, khong tu tham so ham. */
public record AuditRecord(
        String requestId,
        String action,
        String method,
        String arguments,
        String status,
        String result,
        String error,
        long durationMs,
        String thread,
        Instant timestamp) {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
}
