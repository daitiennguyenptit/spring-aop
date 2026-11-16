package com.example.aop.pitfalls.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TraceAspect {

    private final TraceRecorder recorder;

    public TraceAspect(TraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Before("@annotation(com.example.aop.pitfalls.annotation.Traced)")
    public void trace(JoinPoint jp) {
        recorder.record(jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName());
    }
}
