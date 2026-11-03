package org.thelearninghub.patterns.structural;

import java.util.concurrent.atomic.AtomicLong;
import org.thelearninghub.patterns.behavioral.DiscountStrategy;
import org.thelearninghub.patterns.solid.FlatRateTax;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.Notifier;
import org.thelearninghub.patterns.solid.Order;
import org.thelearninghub.patterns.solid.OrderProcessor;
import org.thelearninghub.patterns.solid.PaymentGateway;
import org.thelearninghub.patterns.solid.Receipt;
import org.thelearninghub.patterns.solid.TaxCalculator;

/**
 * Facade: one method for the common case. Behind it sit a discount strategy, an order
 * processor, a tax rule, a payment adapter and a notifier. Callers that need finer control
 * can still use those classes directly; the facade hides them without locking them away.
 */
public final class CheckoutFacade {

    private final OrderProcessor processor;
    private final AtomicLong orderSequence = new AtomicLong();

    public CheckoutFacade(PaymentGateway payments, Notifier notifier, TaxCalculator tax) {
        this.processor = new OrderProcessor(tax, payments, notifier);
    }

    /** The wiring most callers want: legacy payments behind the adapter, 8% flat tax. */
    public static CheckoutFacade withLegacyPayments(Notifier notifier) {
        return new CheckoutFacade(new LegacyPaymentAdapter(new LegacyXmlPaymentService()), notifier, new FlatRateTax(8));
    }

    public Receipt checkout(String customerId, String email, Money subtotal, DiscountStrategy discount) {
        Money discounted = discount.apply(subtotal);
        String orderId = "ORD-" + orderSequence.incrementAndGet();
        return processor.process(new Order(orderId, customerId, email, discounted));
    }
}
