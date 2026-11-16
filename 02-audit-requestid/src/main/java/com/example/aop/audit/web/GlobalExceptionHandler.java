package com.example.aop.audit.web;

import com.example.aop.audit.context.RequestContext;
import com.example.aop.audit.service.InsufficientBalanceException;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Tra requestId trong body loi de khach hang/CSKH doi chieu voi audit log. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<Map<String, String>> insufficient(InsufficientBalanceException ex) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INSUFFICIENT_BALANCE");
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> notFound(NoSuchElementException ex) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String code) {
        String requestId = RequestContext.getRequestId();
        return ResponseEntity.status(status).body(Map.of("code", code, "requestId", requestId == null ? "" : requestId));
    }
}
