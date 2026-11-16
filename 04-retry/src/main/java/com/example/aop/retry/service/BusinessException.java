package com.example.aop.retry.service;

/** Loi nghiep vu (vd: the bi khoa) - retry khong giai quyet duoc. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
