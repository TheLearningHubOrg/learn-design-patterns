package org.thelearninghub.patterns.structural;

import java.util.concurrent.atomic.AtomicInteger;

/** Concrete component. Stands in for a slow network call and counts how often it runs. */
public final class RemoteDataService implements DataService {

    private final AtomicInteger calls = new AtomicInteger();

    @Override
    public String fetch(String key) {
        calls.incrementAndGet();
        return "value-of-" + key;
    }

    public int calls() {
        return calls.get();
    }
}
