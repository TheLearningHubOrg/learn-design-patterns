package org.thelearninghub.patterns.solid;

/** Abstraction owned by the business layer (DIP): it says what it needs, not how it is done. */
@FunctionalInterface
public interface TaxCalculator {
    Money taxOn(Money net);
}
