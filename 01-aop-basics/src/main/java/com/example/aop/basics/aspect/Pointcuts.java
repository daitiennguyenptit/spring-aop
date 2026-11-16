package com.example.aop.basics.aspect;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Tap trung cac pointcut co ten de tai su dung (giong "constant" cho pointcut).
 */
public final class Pointcuts {

    private Pointcuts() {
    }

    /** Moi method public trong package service (va package con). */
    @Pointcut("execution(public * com.example.aop.basics.service..*(..))")
    public static void serviceLayer() {
    }

    /** Chi method placeOrder co tham so (String, int). */
    @Pointcut("execution(* com.example.aop.basics.service.OrderService.placeOrder(String, int))")
    public static void placeOrder() {
    }

    /** Moi join point trong class co @Service. */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public static void anyServiceBean() {
    }
}
