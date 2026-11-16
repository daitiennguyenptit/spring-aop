package com.example.aop.perf.service;

import com.example.aop.perf.annotation.Monitored;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    public String fastReport() {
        return "fast-report";
    }

    @Monitored(slowThresholdMs = 100)
    public String slowReport() {
        simulateWork(150);   // gia lap query cham
        return "slow-report";
    }

    private static void simulateWork(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
