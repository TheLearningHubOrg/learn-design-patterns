package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.thelearninghub.patterns.solid.FlatRateTax;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.Order;
import org.thelearninghub.patterns.solid.OrderProcessor;
import org.thelearninghub.patterns.solid.PaymentResult;
import org.thelearninghub.patterns.solid.Receipt;
import org.thelearninghub.patterns.solid.Shape;
import org.thelearninghub.patterns.solid.TieredTax;

class SolidTest {

    private final List<String> sent = new ArrayList<>();

    @Test
    void moneyRoundsHalfUpAndRejectsNegatives() {
        assertEquals(Money.ofCents(1), Money.ofCents(5).percent(10)); // 0.5 cent rounds up
        assertEquals("12.05", Money.ofCents(1205).toString());
        assertThrows(IllegalArgumentException.class, () -> Money.ofCents(100).minus(Money.ofCents(101)));
    }

    @Test
    void processorChargesTaxAndNotifiesOnlyWhenPaid() {
        OrderProcessor processor = new OrderProcessor(
                new FlatRateTax(10),
                (customer, amount) -> PaymentResult.approved("PAY-1"),
                (to, message) -> sent.add(to + "|" + message));

        Receipt receipt = processor.process(new Order("o-1", "c-1", "a@example.com", Money.ofCents(10_000)));

        assertEquals(Money.ofCents(1_000), receipt.tax());
        assertEquals(Money.ofCents(11_000), receipt.total());
        assertTrue(receipt.paid());
        assertEquals(List.of("a@example.com|Order o-1 confirmed: 110.00"), sent);
    }

    @Test
    void declinedPaymentSendsNothing() {
        OrderProcessor processor = new OrderProcessor(
                new FlatRateTax(0),
                (customer, amount) -> PaymentResult.declined("insufficient-funds"),
                (to, message) -> sent.add(message));

        Receipt receipt = processor.process(new Order("o-2", "c-1", "a@example.com", Money.ofCents(500)));

        assertFalse(receipt.paid());
        assertEquals("insufficient-funds", receipt.reference());
        assertTrue(sent.isEmpty());
    }

    @Test
    void tieredTaxIsANewRuleWithoutTouchingTheProcessor() {
        TieredTax tax = new TieredTax(Money.ofCents(1_000), 10);
        assertEquals(Money.ZERO, tax.taxOn(Money.ofCents(999)));
        assertEquals(Money.ofCents(100), tax.taxOn(Money.ofCents(1_000)));
    }

    @Test
    void shapesKeepTheirInvariantsBecauseTheyAreImmutable() {
        Shape.Rectangle wide = new Shape.Rectangle(5, 10).withWidth(6);
        assertEquals(60, wide.area());
        assertEquals(49, new Shape.Square(7).area());
    }
}
