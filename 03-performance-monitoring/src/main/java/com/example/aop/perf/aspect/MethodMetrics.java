package com.example.aop.perf.aspect;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.stereotype.Component;

/** Thong ke thread-safe: so lan goi, tong/max thoi gian, so lan cham. (Thuc te: Micrometer Timer.) */
@Component
public class MethodMetrics {

    public record Snapshot(long calls, long totalMs, long maxMs, long slowCalls, long avgMs) {
    }

    private static final class Stat {
        final LongAdder calls = new LongAdder();
        final LongAdder totalMs = new LongAdder();
        final LongAccumulator maxMs = new LongAccumulator(Math::max, 0);
        final LongAdder slowCalls = new LongAdder();
    }

    private final Map<String, Stat> stats = new ConcurrentHashMap<>();

    public void record(String method, long durationMs, boolean slow) {
        Stat stat = stats.computeIfAbsent(method, k -> new Stat());
        stat.calls.increment();
        stat.totalMs.add(durationMs);
        stat.maxMs.accumulate(durationMs);
        if (slow) {
            stat.slowCalls.increment();
        }
    }

    public Map<String, Snapshot> snapshot() {
        Map<String, Snapshot> result = new TreeMap<>();
        stats.forEach((method, s) -> {
            long calls = s.calls.sum();
            long total = s.totalMs.sum();
            result.put(method, new Snapshot(calls, total, s.maxMs.get(), s.slowCalls.sum(), calls == 0 ? 0 : total / calls));
        });
        return result;
    }

    public void reset() {
        stats.clear();
    }
}
