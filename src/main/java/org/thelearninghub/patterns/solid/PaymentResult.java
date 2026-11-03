package org.thelearninghub.patterns.solid;

/** Outcome of a charge. {@code reference} is the provider's id, or the decline reason. */
public record PaymentResult(boolean approved, String reference) {

    public static PaymentResult approved(String reference) {
        return new PaymentResult(true, reference);
    }

    public static PaymentResult declined(String reason) {
        return new PaymentResult(false, reason);
    }
}
