package com.example.aop.retry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 04: Tu dong retry khi goi he thong ngoai bi loi tam thoi (timeout, 503...).
 * Chay: mvn -pl 04-retry spring-boot:run ; curl -X POST localhost:8084/payments/PAY-1
 */
@SpringBootApplication
public class RetryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }
}
