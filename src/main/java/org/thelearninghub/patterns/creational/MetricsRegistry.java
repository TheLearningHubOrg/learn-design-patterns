package org.thelearninghub.patterns.creational;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Enum singleton. The JVM creates {@code INSTANCE} exactly once, when the enum class is
 * initialised, and serialization or reflection cannot create a second one.
 */
public enum MetricsRegistry {
    INSTANCE;

    private final ConcurrentHashMap<String, LongAdder> counters = new ConcurrentHashMap<>();

    public void increment(String name) {
        counters.computeIfAbsent(name, key -> new LongAdder()).increment();
    }

    public long count(String name) {
        LongAdder adder = counters.get(name);
        return adder == null ? 0 : adder.sum();
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> copy = new TreeMap<>();
        counters.forEach((name, adder) -> copy.put(name, adder.sum()));
        return copy;
    }

    /** Tests call this; global state that cannot be reset is the classic Singleton trap. */
    public void reset() {
        counters.clear();
    }
}
