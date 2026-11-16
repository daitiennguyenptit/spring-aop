package com.example.aop.audit.context;

import java.util.function.Supplier;
import org.slf4j.MDC;

/**
 * "Tui" chua requestId gan voi THREAD dang xu ly request.
 *
 * - ThreadLocal: code nghiep vu / aspect doc duoc o bat ky tang nao ma khong can truyen tham so.
 * - MDC: logback tu in requestId vao MOI dong log (xem logging.pattern trong application.yml).
 *
 * Bat buoc clear() sau moi request vi thread cua Tomcat duoc tai su dung (thread pool) -
 * neu quen se "ro ri" requestId sang request khac.
 */
public final class RequestContext {

    public static final String REQUEST_ID = "requestId";
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private RequestContext() {
    }

    public static void setRequestId(String requestId) {
        HOLDER.set(requestId);
        MDC.put(REQUEST_ID, requestId);
    }

    public static String getRequestId() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
        MDC.remove(REQUEST_ID);
    }

    /**
     * Dung cho entry point KHONG phai HTTP (scheduled job, Kafka consumer, CLI...):
     * gan requestId trong pham vi mot khoi code roi tra lai trang thai cu.
     */
    public static <T> T callWithRequestId(String requestId, Supplier<T> action) {
        String previous = HOLDER.get();
        setRequestId(requestId);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                clear();
            } else {
                setRequestId(previous);
            }
        }
    }
}
