package com.example.aop.pitfalls.aspect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class TraceRecorder {

    private final List<String> calls = new CopyOnWriteArrayList<>();

    public void record(String call) {
        calls.add(call);
    }

    public List<String> calls() {
        return List.copyOf(calls);
    }

    public void clear() {
        calls.clear();
    }
}
