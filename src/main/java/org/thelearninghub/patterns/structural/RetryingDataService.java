package org.thelearninghub.patterns.structural;

import java.util.Objects;

/**
 * Stage 3 exercise solution: a third decorator that retries failed calls. It composes
 * with caching and logging in any order without changing either of them.
 */
public final class RetryingDataService implements DataService {

    private final DataService inner;
    private final int maxAttempts;

    public RetryingDataService(DataService inner, int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1, was " + maxAttempts);
        }
        this.inner = Objects.requireNonNull(inner, "inner");
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String fetch(String key) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return inner.fetch(key);
            } catch (RuntimeException e) {
                last = e;
            }
        }
        throw last;
    }
}
