package com.example.aop.perf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Ghi de nguong "cham" cho mot method cu the. Khong co annotation -> dung nguong mac dinh. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {
    long slowThresholdMs();
}
