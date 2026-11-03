package org.thelearninghub.patterns.solid;

import java.util.Objects;

/**
 * High-level policy: tax, charge, notify. It depends only on the three interfaces it
 * receives, so each collaborator can change (or be faked in a test) independently.
 */
public final class OrderProcessor {

    private final TaxCalculator tax;
    private final PaymentGateway payments;
    private final Notifier notifier;

    public OrderProcessor(TaxCalculator tax, PaymentGateway payments, Notifier notifier) {
        this.tax = Objects.requireNonNull(tax, "tax");
        this.payments = Objects.requireNonNull(payments, "payments");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public Receipt process(Order order) {
        Money taxAmount = tax.taxOn(order.net());
        Money total = order.net().plus(taxAmount);
        PaymentResult result = payments.charge(order.customerId(), total);
        if (result.approved()) {
            notifier.send(order.email(), "Order " + order.id() + " confirmed: " + total);
        }
        return new Receipt(order.id(), order.net(), taxAmount, total, result.approved(), result.reference());
    }
}
