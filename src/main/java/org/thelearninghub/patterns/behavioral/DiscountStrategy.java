package org.thelearninghub.patterns.behavioral;

import java.util.Locale;
import org.thelearninghub.patterns.solid.Money;

/**
 * Strategy as a functional interface: each pricing rule is a lambda, and the caller picks
 * one at runtime. Stage 4 covers the pattern in depth.
 */
@FunctionalInterface
public interface DiscountStrategy {

    Money apply(Money subtotal);

    static DiscountStrategy none() {
        return subtotal -> subtotal;
    }

    static DiscountStrategy percentOff(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be between 0 and 100, was " + percent);
        }
        return subtotal -> subtotal.minus(subtotal.percent(percent));
    }

    static DiscountStrategy forTier(String tier) {
        return switch (tier.toLowerCase(Locale.ROOT)) {
            case "regular" -> none();
            case "member" -> percentOff(5);
            case "vip" -> percentOff(20);
            default -> throw new IllegalArgumentException("Unknown tier: " + tier);
        };
    }
}
