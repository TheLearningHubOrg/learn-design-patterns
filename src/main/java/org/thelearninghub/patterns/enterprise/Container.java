package org.thelearninghub.patterns.enterprise;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A minimal dependency-injection container keyed by type. Each registration is a factory
 * that receives the container, so a factory can resolve its own dependencies. Singletons
 * are built once on first use; transients are built on every {@link #resolve} call.
 * Cycles are detected and reported with the full path instead of a StackOverflowError.
 */
public final class Container {

    public enum Lifetime { SINGLETON, TRANSIENT }

    private record Registration<T>(Function<Container, T> factory, Lifetime lifetime) {
    }

    private final Map<Class<?>, Registration<?>> registrations = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Deque<Class<?>> resolving = new ArrayDeque<>();

    public <T> Container singleton(Class<T> type, Function<Container, T> factory) {
        return register(type, factory, Lifetime.SINGLETON);
    }

    public <T> Container transientService(Class<T> type, Function<Container, T> factory) {
        return register(type, factory, Lifetime.TRANSIENT);
    }

    private <T> Container register(Class<T> type, Function<Container, T> factory, Lifetime lifetime) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(factory, "factory");
        if (registrations.putIfAbsent(type, new Registration<>(factory, lifetime)) != null) {
            throw new IllegalStateException(type.getSimpleName() + " is already registered");
        }
        return this;
    }

    public synchronized <T> T resolve(Class<T> type) {
        Registration<?> registration = registrations.get(type);
        if (registration == null) {
            throw new IllegalStateException("No registration for " + type.getSimpleName());
        }
        if (registration.lifetime() == Lifetime.SINGLETON && singletons.containsKey(type)) {
            return type.cast(singletons.get(type));
        }
        if (resolving.contains(type)) {
            StringBuilder path = new StringBuilder();
            resolving.descendingIterator().forEachRemaining(t -> path.append(t.getSimpleName()).append(" -> "));
            throw new IllegalStateException("Dependency cycle: " + path + type.getSimpleName());
        }
        resolving.push(type);
        try {
            T instance = type.cast(registration.factory().apply(this));
            if (registration.lifetime() == Lifetime.SINGLETON) {
                singletons.put(type, instance);
            }
            return instance;
        } finally {
            resolving.pop();
        }
    }
}
