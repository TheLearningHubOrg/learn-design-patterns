package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.thelearninghub.patterns.behavioral.DiscountStrategy;
import org.thelearninghub.patterns.behavioral.EventBus;
import org.thelearninghub.patterns.behavioral.MiddlewareChain;
import org.thelearninghub.patterns.solid.Money;

class BehavioralTest {

    @Test
    void strategiesAreInterchangeable() {
        Money subtotal = Money.ofCents(10_000);
        assertEquals(Money.ofCents(10_000), DiscountStrategy.forTier("regular").apply(subtotal));
        assertEquals(Money.ofCents(9_500), DiscountStrategy.forTier("member").apply(subtotal));
        assertEquals(Money.ofCents(8_000), DiscountStrategy.forTier("VIP").apply(subtotal));
        assertThrows(IllegalArgumentException.class, () -> DiscountStrategy.forTier("gold"));
    }

    @Test
    void observersReceiveEventsUntilTheyUnsubscribe() {
        EventBus<String> bus = new EventBus<>();
        List<String> seen = new ArrayList<>();
        Runnable unsubscribe = bus.subscribe(seen::add);

        bus.publish("order.created");
        unsubscribe.run();
        bus.publish("order.shipped");

        assertEquals(List.of("order.created"), seen);
        assertEquals(0, bus.subscriberCount());
    }

    @Test
    void middlewareCanStopTheChain() {
        MiddlewareChain<List<String>> chain = new MiddlewareChain<List<String>>()
                .use((steps, next) -> { steps.add("auth"); next.run(); })
                .use((steps, next) -> steps.add("rate-limited")) // never calls next
                .use((steps, next) -> steps.add("controller"));

        assertEquals(List.of("auth", "rate-limited"), chain.execute(new ArrayList<>()));
    }
}
