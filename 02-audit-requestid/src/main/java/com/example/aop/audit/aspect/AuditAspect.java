package com.example.aop.audit.aspect;

import com.example.aop.audit.annotation.Audited;
import com.example.aop.audit.annotation.Sensitive;
import com.example.aop.audit.context.RequestContext;
import com.example.aop.audit.store.AuditLogStore;
import com.example.aop.audit.store.AuditRecord;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.StringJoiner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect ghi audit cho MOI method co @Audited.
 *
 * Diem mau chot: requestId duoc doc tu RequestContext (ThreadLocal) - do Filter gan tu dau request -
 * nen cac ham nghiep vu KHONG can tham so requestId.
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)   // boc ngoai cac aspect khac (vd @Transactional) de do ca thoi gian commit
public class AuditAspect {

    private static final Logger AUDIT_LOG = LoggerFactory.getLogger("AUDIT");
    private static final String NO_REQUEST = "NO-REQUEST-ID";
    private static final int MAX_VALUE_LENGTH = 200;

    private final AuditLogStore store;

    public AuditAspect(AuditLogStore store) {
        this.store = store;
    }

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String requestId = currentRequestId();
        String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String arguments = describeArguments(signature.getMethod(), pjp.getArgs());
        long start = System.nanoTime();

        try {
            Object result = pjp.proceed();
            String resultText = audited.logResult() ? abbreviate(String.valueOf(result)) : null;
            write(new AuditRecord(requestId, audited.action(), method, arguments, AuditRecord.SUCCESS,
                    resultText, null, elapsedMs(start), Thread.currentThread().getName(), Instant.now()));
            return result;
        } catch (Throwable ex) {
            write(new AuditRecord(requestId, audited.action(), method, arguments, AuditRecord.FAILURE,
                    null, ex.getClass().getSimpleName() + ": " + abbreviate(ex.getMessage()),
                    elapsedMs(start), Thread.currentThread().getName(), Instant.now()));
            throw ex;   // KHONG nuot exception - aspect audit khong duoc thay doi hanh vi nghiep vu
        }
    }

    private void write(AuditRecord auditRecord) {
        try {
            store.save(auditRecord);
            AUDIT_LOG.info("action={} method={} status={} durationMs={} args={}{}",
                    auditRecord.action(), auditRecord.method(), auditRecord.status(), auditRecord.durationMs(),
                    auditRecord.arguments(), auditRecord.error() == null ? "" : " error=" + auditRecord.error());
        } catch (RuntimeException storeFailure) {
            // Loi ghi audit khong duoc lam hong giao dich nghiep vu (tuy chinh sach: co he thong yeu cau fail-closed)
            AUDIT_LOG.error("Cannot persist audit record {}", auditRecord, storeFailure);
        }
    }

    private static String currentRequestId() {
        String requestId = RequestContext.getRequestId();
        return requestId != null ? requestId : NO_REQUEST;
    }

    /** In ten tham so + gia tri; che tham so co @Sensitive. Can compile voi -parameters (Spring Boot parent da bat). */
    static String describeArguments(Method method, Object[] args) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        var params = method.getParameters();
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (int i = 0; i < args.length; i++) {
            boolean sensitive = false;
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof Sensitive) {
                    sensitive = true;
                    break;
                }
            }
            joiner.add(params[i].getName() + "=" + (sensitive ? "***" : abbreviate(String.valueOf(args[i]))));
        }
        return joiner.toString();
    }

    private static String abbreviate(String value) {
        if (value == null) {
            return null;
        }
        String singleLine = value.replaceAll("[\\r\\n]", " ");   // chong log injection
        return singleLine.length() <= MAX_VALUE_LENGTH ? singleLine : singleLine.substring(0, MAX_VALUE_LENGTH) + "...";
    }

    private static long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
