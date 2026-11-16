package com.example.aop.perf.aspect;

import com.example.aop.perf.annotation.Monitored;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * Ap dung cho MOI bean @Service trong package service (khong can gan annotation),
 * @Monitored chi dung de ghi de nguong.
 */
@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    private final MethodMetrics metrics;
    private final long defaultThresholdMs;

    public PerformanceAspect(MethodMetrics metrics, @Value("${perf.slow-threshold-ms:500}") long defaultThresholdMs) {
        this.metrics = metrics;
        this.defaultThresholdMs = defaultThresholdMs;
    }

    @Around("within(@org.springframework.stereotype.Service *) && within(com.example.aop.perf.service..*)")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String name = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            long threshold = thresholdOf(signature.getMethod());
            boolean slow = durationMs >= threshold;
            metrics.record(name, durationMs, slow);
            if (slow) {
                log.warn("SLOW method {} took {} ms (threshold {} ms)", name, durationMs, threshold);
            } else {
                log.debug("{} took {} ms", name, durationMs);
            }
        }
    }

    private long thresholdOf(Method method) {
        Monitored monitored = AnnotationUtils.findAnnotation(method, Monitored.class);
        return monitored != null ? monitored.slowThresholdMs() : defaultThresholdMs;
    }
}
