package com.example.aop.perf.web;

import com.example.aop.perf.aspect.MethodMetrics;
import com.example.aop.perf.service.ReportService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    private final ReportService reportService;
    private final MethodMetrics metrics;

    public ReportController(ReportService reportService, MethodMetrics metrics) {
        this.reportService = reportService;
        this.metrics = metrics;
    }

    @GetMapping("/reports/fast")
    public String fast() {
        return reportService.fastReport();
    }

    @GetMapping("/reports/slow")
    public String slow() {
        return reportService.slowReport();
    }

    @GetMapping("/metrics/methods")
    public Map<String, MethodMetrics.Snapshot> methods() {
        return metrics.snapshot();
    }
}
