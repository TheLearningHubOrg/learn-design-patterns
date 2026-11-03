package org.thelearninghub.patterns.solid;

/**
 * A non-negative amount of money stored as whole cents.
 *
 * <p>Using a value object instead of {@code double} keeps rounding rules in one place
 * and makes negative or overflowing amounts impossible to construct.
 */
public record Money(long cents) implements Comparable<Money> {

    public static final Money ZERO = new Money(0);

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("Money cannot be negative: " + cents);
        }
    }

    public static Money ofCents(long cents) {
        return new Money(cents);
    }

    public Money plus(Money other) {
        return new Money(Math.addExact(cents, other.cents));
    }

    public Money minus(Money other) {
        return new Money(cents - other.cents);
    }

    /** Returns {@code percent}% of this amount, rounded half up to the nearest cent. */
    public Money percent(int percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("percent must be >= 0, was " + percent);
        }
        return new Money((Math.multiplyExact(cents, percent) + 50) / 100);
    }

    @Override
    public int compareTo(Money other) {
        return Long.compare(cents, other.cents);
    }

    @Override
    public String toString() {
        return String.format("%d.%02d", cents / 100, cents % 100);
    }
}
