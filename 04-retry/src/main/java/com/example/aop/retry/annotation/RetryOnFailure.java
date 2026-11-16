package com.example.aop.retry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retry method khi gap exception thuoc retryFor. CHI dung cho thao tac idempotent
 * (hoac co idempotency key) - retry mot lenh chuyen tien khong idempotent co the tru tien 2 lan!
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnFailure {

    int maxAttempts() default 3;

    /** Backoff khoi dau, nhan doi moi lan (exponential backoff). */
    long backoffMs() default 100;

    Class<? extends Throwable>[] retryFor() default {RuntimeException.class};
}
