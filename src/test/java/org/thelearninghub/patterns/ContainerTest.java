package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.thelearninghub.patterns.enterprise.Container;
import org.thelearninghub.patterns.solid.FlatRateTax;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.Notifier;
import org.thelearninghub.patterns.solid.Order;
import org.thelearninghub.patterns.solid.OrderProcessor;
import org.thelearninghub.patterns.solid.PaymentGateway;
import org.thelearninghub.patterns.solid.TaxCalculator;
import org.thelearninghub.patterns.structural.LegacyPaymentAdapter;
import org.thelearninghub.patterns.structural.LegacyXmlPaymentService;

class ContainerTest {

    @Test
    void wiresAnObjectGraphFromRegistrations() {
        List<String> sent = new ArrayList<>();
        Container container = new Container()
                .singleton(TaxCalculator.class, c -> new FlatRateTax(10))
                .singleton(LegacyXmlPaymentService.class, c -> new LegacyXmlPaymentService())
                .singleton(PaymentGateway.class, c -> new LegacyPaymentAdapter(c.resolve(LegacyXmlPaymentService.class)))
                .singleton(Notifier.class, c -> (to, msg) -> sent.add(msg))
                .transientService(OrderProcessor.class, c -> new OrderProcessor(
                        c.resolve(TaxCalculator.class), c.resolve(PaymentGateway.class), c.resolve(Notifier.class)));

        OrderProcessor first = container.resolve(OrderProcessor.class);
        OrderProcessor second = container.resolve(OrderProcessor.class);

        assertNotSame(first, second);
        assertSame(container.resolve(PaymentGateway.class), container.resolve(PaymentGateway.class));
        first.process(new Order("o-1", "c-1", "a@example.com", Money.ofCents(1_000)));
        assertEquals(List.of("Order o-1 confirmed: 11.00"), sent);
    }

    @Test
    void reportsCyclesWithThePath() {
        Container container = new Container()
                .singleton(TaxCalculator.class, c -> {
                    c.resolve(PaymentGateway.class);
                    return new FlatRateTax(1);
                })
                .singleton(PaymentGateway.class, c -> {
                    c.resolve(TaxCalculator.class);
                    return (customer, amount) -> null;
                });

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> container.resolve(TaxCalculator.class));
        assertEquals("Dependency cycle: TaxCalculator -> PaymentGateway -> TaxCalculator", e.getMessage());
    }

    @Test
    void unknownAndDuplicateRegistrationsFailFast() {
        Container container = new Container().singleton(TaxCalculator.class, c -> new FlatRateTax(1));
        assertThrows(IllegalStateException.class, () -> container.resolve(Notifier.class));
        assertThrows(IllegalStateException.class, () -> container.singleton(TaxCalculator.class, c -> new FlatRateTax(2)));
    }
}
