package org.thelearninghub.patterns.solid;

/** Port for taking money. Stripe, a legacy XML service or a test fake can sit behind it. */
@FunctionalInterface
public interface PaymentGateway {
    PaymentResult charge(String customerId, Money amount);
}
