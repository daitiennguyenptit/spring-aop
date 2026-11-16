package com.example.aop.retry.service;

/** Loi tam thoi (timeout, 503) - nen retry. */
public class TransientException extends RuntimeException {
    public TransientException(String message) {
        super(message);
    }
}
