package com.example.aop.authz.aspect;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("Authentication required");
    }
}
