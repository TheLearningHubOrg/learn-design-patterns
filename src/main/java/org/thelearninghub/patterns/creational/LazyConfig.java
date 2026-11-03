package org.thelearninghub.patterns.creational;

import java.util.Map;

/**
 * Initialization-on-demand holder idiom. {@code Holder} is not initialised until
 * {@link #get()} first touches it, and class initialisation is thread-safe by the JLS,
 * so no {@code synchronized} or {@code volatile} is needed.
 */
public final class LazyConfig {

    private final Map<String, String> values;

    private LazyConfig(Map<String, String> values) {
        this.values = Map.copyOf(values);
    }

    private static final class Holder {
        private static final LazyConfig INSTANCE = new LazyConfig(System.getenv());
    }

    public static LazyConfig get() {
        return Holder.INSTANCE;
    }

    /** For tests: build a config from explicit values instead of the process environment. */
    public static LazyConfig of(Map<String, String> values) {
        return new LazyConfig(values);
    }

    public String value(String key, String fallback) {
        String found = values.get(key);
        return found == null || found.isBlank() ? fallback : found;
    }

    public int intValue(String key, int fallback) {
        String raw = value(key, null);
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " must be an integer, was '" + raw + "'", e);
        }
    }
}
