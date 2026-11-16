package com.example.aop.perf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 03: Do thoi gian thuc thi + canh bao method cham, khong sua code nghiep vu.
 * Chay: mvn -pl 03-performance-monitoring spring-boot:run
 *   curl localhost:8083/reports/fast ; curl localhost:8083/reports/slow ; curl localhost:8083/metrics/methods
 */
@SpringBootApplication
public class PerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApplication.class, args);
    }
}
