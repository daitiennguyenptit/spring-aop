package com.example.aop.audit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Danh dau method can ghi audit log. Chi can gan annotation - khong sua chu ky ham.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {

    /** Ma hanh dong nghiep vu, vd: TRANSFER, VIEW_BALANCE. */
    String action();

    /** Co ghi gia tri tra ve vao audit hay khong (tat voi du lieu lon/nhay cam). */
    boolean logResult() default false;
}
