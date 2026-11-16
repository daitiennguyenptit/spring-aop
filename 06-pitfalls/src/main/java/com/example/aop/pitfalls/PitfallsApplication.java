package com.example.aop.pitfalls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Demo 06: Nhung truong hop AOP "im lang khong chay" va cach sua.
 * exposeProxy = true de dung duoc AopContext.currentProxy() (cach sua so 2).
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class PitfallsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PitfallsApplication.class, args);
    }
}
