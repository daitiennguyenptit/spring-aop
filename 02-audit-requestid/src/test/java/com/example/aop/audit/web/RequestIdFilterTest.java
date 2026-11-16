package com.example.aop.audit.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestIdFilterTest {

    @Test
    void giuLaiHeaderHopLe() {
        assertThat(RequestIdFilter.resolveRequestId("gw-2026-11-16.abc_1")).isEqualTo("gw-2026-11-16.abc_1");
    }

    @Test
    void tuChoiHeaderCoTheGayLogInjection() {
        String malicious = "abc\r\nFAKE AUDIT action=TRANSFER status=SUCCESS";
        assertThat(RequestIdFilter.resolveRequestId(malicious)).isNotEqualTo(malicious).hasSize(36);
        assertThat(RequestIdFilter.resolveRequestId("x".repeat(65))).hasSize(36);
        assertThat(RequestIdFilter.resolveRequestId(null)).hasSize(36);
    }
}
