package com.example.aop.basics.aspect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Ghi lai thu tu cac advice duoc goi de quan sat/test. */
@Component
public class AdviceTrace {

    private final List<String> events = new CopyOnWriteArrayList<>();

    public void add(String event) {
        events.add(event);
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    public void clear() {
        events.clear();
    }
}
