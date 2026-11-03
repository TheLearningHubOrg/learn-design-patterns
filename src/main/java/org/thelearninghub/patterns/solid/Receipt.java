package org.thelearninghub.patterns.solid;

public record Receipt(String orderId, Money net, Money tax, Money total, boolean paid, String reference) {
}
