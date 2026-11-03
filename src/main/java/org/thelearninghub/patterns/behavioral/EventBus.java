package org.thelearninghub.patterns.behavioral;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-process Observer. Subscribing returns a handle that unsubscribes, so listeners
 * can always be removed (forgotten listeners are the classic Observer memory leak).
 */
public final class EventBus<E> {

    private final List<Consumer<? super E>> subscribers = new CopyOnWriteArrayList<>();

    public Runnable subscribe(Consumer<? super E> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        subscribers.add(subscriber);
        return () -> subscribers.remove(subscriber);
    }

    public void publish(E event) {
        for (Consumer<? super E> subscriber : subscribers) {
            subscriber.accept(event);
        }
    }

    public int subscriberCount() {
        return subscribers.size();
    }
}
