package org.thelearninghub.patterns.solid;

/** One tax rate for every amount. */
public record FlatRateTax(int percent) implements TaxCalculator {

    public FlatRateTax {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be between 0 and 100, was " + percent);
        }
    }

    @Override
    public Money taxOn(Money net) {
        return net.percent(percent);
    }
}
