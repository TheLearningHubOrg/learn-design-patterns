package org.thelearninghub.patterns.catalog;

import java.util.List;
import java.util.Locale;

/** The patterns this guide covers, the stage that teaches each one, and its intent. */
public final class PatternCatalog {

    public enum Category { CREATIONAL, STRUCTURAL, BEHAVIORAL, ENTERPRISE }

    public record Entry(String name, Category category, int stage, String intent) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry("Singleton", Category.CREATIONAL, 2, "One instance with a well-known access point"),
            new Entry("Factory Method", Category.CREATIONAL, 2, "Let a subclass decide which product to create"),
            new Entry("Abstract Factory", Category.CREATIONAL, 2, "Create families of related products together"),
            new Entry("Builder", Category.CREATIONAL, 2, "Assemble a complex object step by step, validate once"),
            new Entry("Prototype", Category.CREATIONAL, 2, "Create new objects by copying a configured one"),
            new Entry("Adapter", Category.STRUCTURAL, 3, "Make an incompatible interface fit the one clients expect"),
            new Entry("Bridge", Category.STRUCTURAL, 3, "Vary an abstraction and its implementation independently"),
            new Entry("Composite", Category.STRUCTURAL, 3, "Treat single objects and trees of objects the same way"),
            new Entry("Decorator", Category.STRUCTURAL, 3, "Add behaviour by wrapping, without subclassing"),
            new Entry("Facade", Category.STRUCTURAL, 3, "One simple entry point to a complex subsystem"),
            new Entry("Flyweight", Category.STRUCTURAL, 3, "Share intrinsic state across many fine-grained objects"),
            new Entry("Proxy", Category.STRUCTURAL, 3, "A stand-in that controls access to the real object"),
            new Entry("Observer", Category.BEHAVIORAL, 4, "Notify many dependents when one subject changes"),
            new Entry("Strategy", Category.BEHAVIORAL, 4, "Swap one algorithm for another at runtime"),
            new Entry("Command", Category.BEHAVIORAL, 4, "Turn a request into an object you can queue or undo"),
            new Entry("State", Category.BEHAVIORAL, 4, "Change behaviour when internal state changes"),
            new Entry("Chain of Responsibility", Category.BEHAVIORAL, 5, "Pass a request along handlers until one acts"),
            new Entry("Mediator", Category.BEHAVIORAL, 5, "Route peer interactions through one coordinator"),
            new Entry("Template Method", Category.BEHAVIORAL, 5, "Fix an algorithm's skeleton, let subclasses fill steps"),
            new Entry("Iterator", Category.BEHAVIORAL, 5, "Traverse a collection without exposing its structure"),
            new Entry("Visitor", Category.BEHAVIORAL, 5, "Add operations to a class hierarchy via double dispatch"),
            new Entry("Memento", Category.BEHAVIORAL, 5, "Capture and restore state without breaking encapsulation"),
            new Entry("Interpreter", Category.BEHAVIORAL, 5, "Represent a small grammar as a class per rule"),
            new Entry("Repository", Category.ENTERPRISE, 6, "Collection-like access to persisted aggregates"),
            new Entry("Unit of Work", Category.ENTERPRISE, 6, "Track changes and commit them in one transaction"),
            new Entry("CQRS", Category.ENTERPRISE, 6, "Separate the write model from the read model"),
            new Entry("Event Sourcing", Category.ENTERPRISE, 6, "Store state as an append-only log of events"),
            new Entry("Dependency Injection", Category.ENTERPRISE, 6, "Supply collaborators from outside the object"));

    private PatternCatalog() {
    }

    public static List<Entry> all() {
        return ENTRIES;
    }

    public static List<Entry> byCategory(String category) {
        Category wanted = Category.valueOf(category.toUpperCase(Locale.ROOT));
        return ENTRIES.stream().filter(e -> e.category() == wanted).toList();
    }
}
