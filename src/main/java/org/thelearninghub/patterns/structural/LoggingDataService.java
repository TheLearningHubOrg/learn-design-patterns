package org.thelearninghub.patterns.structural;

import java.util.Objects;
import java.util.function.Consumer;

/** Decorator that records every call before and after delegating. */
public final class LoggingDataService implements DataService {

    private final DataService inner;
    private final Consumer<String> log;

    public LoggingDataService(DataService inner, Consumer<String> log) {
        this.inner = Objects.requireNonNull(inner, "inner");
        this.log = Objects.requireNonNull(log, "log");
    }

    @Override
    public String fetch(String key) {
        log.accept("fetch(" + key + ") start");
        try {
            String value = inner.fetch(key);
            log.accept("fetch(" + key + ") ok");
            return value;
        } catch (RuntimeException e) {
            log.accept("fetch(" + key + ") failed: " + e.getMessage());
            throw e;
        }
    }
}
