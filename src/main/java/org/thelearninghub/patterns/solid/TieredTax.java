package org.thelearninghub.patterns.solid;

/**
 * Stage 1 exercise solution: a new tax rule added without editing {@link OrderProcessor} (OCP).
 * Amounts below the threshold are tax free; the rest pay the full rate.
 */
public record TieredTax(Money threshold, int percent) implements TaxCalculator {

    @Override
    public Money taxOn(Money net) {
        return net.compareTo(threshold) < 0 ? Money.ZERO : net.percent(percent);
    }
}
