package com.example.aop.basics.aspect;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Minh hoa du 5 loai advice tren cung mot pointcut.
 *
 * Thu tu trong CUNG mot aspect (Spring >= 5.2.7):
 *   @Around(truoc) -> @Before -> [method] -> @AfterReturning/@AfterThrowing -> @After -> @Around(sau)
 */
@Aspect
@Component
public class TracingAspect {

    private static final Logger log = LoggerFactory.getLogger(TracingAspect.class);
    private final AdviceTrace trace;

    public TracingAspect(AdviceTrace trace) {
        this.trace = trace;
    }

    @Around("com.example.aop.basics.aspect.Pointcuts.placeOrder()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        trace.add("around-before");
        try {
            return pjp.proceed();          // goi tiep chuoi advice / target
        } finally {
            trace.add("around-after");
        }
    }

    @Before("com.example.aop.basics.aspect.Pointcuts.placeOrder()")
    public void before(JoinPoint jp) {
        trace.add("before");
        log.info("@Before {} args={}", jp.getSignature().toShortString(), Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "com.example.aop.basics.aspect.Pointcuts.placeOrder()", returning = "result")
    public void afterReturning(Object result) {
        trace.add("afterReturning:" + result);
    }

    @AfterThrowing(pointcut = "com.example.aop.basics.aspect.Pointcuts.placeOrder()", throwing = "ex")
    public void afterThrowing(Exception ex) {
        trace.add("afterThrowing:" + ex.getClass().getSimpleName());
    }

    @After("com.example.aop.basics.aspect.Pointcuts.placeOrder()")
    public void after() {
        trace.add("after");   // giong finally: luon chay
    }

    /** Bind tham so bang args(): chi match method co (String, int) va lay gia tri truc tiep. */
    @Before("com.example.aop.basics.aspect.Pointcuts.serviceLayer() && args(sku, quantity)")
    public void bindArgs(String sku, int quantity) {
        log.info("@Before(args binding) sku={} quantity={}", sku, quantity);
    }
}
