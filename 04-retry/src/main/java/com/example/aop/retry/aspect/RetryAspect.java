package com.example.aop.retry.aspect;

import com.example.aop.retry.annotation.RetryOnFailure;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ProceedingJoinPoint.proceed() co the goi NHIEU lan -> chinh la nen tang cua retry.
 *
 * @Order(HIGHEST_PRECEDENCE): retry phai boc NGOAI @Transactional, de moi lan thu la mot transaction moi.
 * (Neu nam trong transaction, transaction da bi danh dau rollback-only thi retry vo nghia.)
 *
 * KHONG dung dung Ordered.HIGHEST_PRECEDENCE: ExposeInvocationInterceptor cua Spring co order
 * HIGHEST_PRECEDENCE + 1 va phai chay truoc moi advice co binding tham so (@annotation(retry)),
 * neu khong se loi "JoinPointMatch was NOT bound in invocation".
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class RetryAspect {

    private static final Logger log = LoggerFactory.getLogger(RetryAspect.class);

    @Around("@annotation(retry)")
    public Object retry(ProceedingJoinPoint pjp, RetryOnFailure retry) throws Throwable {
        long backoff = retry.backoffMs();
        for (int attempt = 1; ; attempt++) {
            try {
                return pjp.proceed();
            } catch (Throwable ex) {
                if (!isRetryable(ex, retry) || attempt >= retry.maxAttempts()) {
                    log.error("{} failed after {} attempt(s): {}", pjp.getSignature().toShortString(), attempt, ex.toString());
                    throw ex;
                }
                log.warn("{} attempt {}/{} failed ({}), retry in {} ms",
                        pjp.getSignature().toShortString(), attempt, retry.maxAttempts(), ex.getMessage(), backoff);
                Thread.sleep(backoff);
                backoff *= 2;
            }
        }
    }

    private static boolean isRetryable(Throwable ex, RetryOnFailure retry) {
        for (Class<? extends Throwable> type : retry.retryFor()) {
            if (type.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }
}
