package com.example.aop.perf;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.aop.perf.aspect.MethodMetrics;
import com.example.aop.perf.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PerformanceAspectTest {

    @Autowired
    ReportService reportService;
    @Autowired
    MethodMetrics metrics;

    @BeforeEach
    void reset() {
        metrics.reset();
    }

    @Test
    void doDuocThoiGianVaPhatHienMethodCham() {
        reportService.fastReport();
        reportService.fastReport();
        reportService.slowReport();

        var snapshot = metrics.snapshot();
        assertThat(snapshot.get("ReportService.fastReport").calls()).isEqualTo(2);
        assertThat(snapshot.get("ReportService.fastReport").slowCalls()).isZero();

        var slow = snapshot.get("ReportService.slowReport");
        assertThat(slow.calls()).isEqualTo(1);
        assertThat(slow.slowCalls()).isEqualTo(1);
        assertThat(slow.maxMs()).isGreaterThanOrEqualTo(150);
    }
}
