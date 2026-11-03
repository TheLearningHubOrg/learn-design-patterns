package org.thelearninghub.patterns.structural;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Decorator that remembers answers. Same interface in, same interface out. */
public final class CachingDataService implements DataService {

    private final DataService inner;
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    public CachingDataService(DataService inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    @Override
    public String fetch(String key) {
        // computeIfAbsent runs inner.fetch at most once per key, even under concurrency.
        return cache.computeIfAbsent(key, inner::fetch);
    }

    public int size() {
        return cache.size();
    }
}
