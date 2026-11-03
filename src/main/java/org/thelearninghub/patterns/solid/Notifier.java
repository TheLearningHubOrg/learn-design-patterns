package org.thelearninghub.patterns.solid;

/** Port for telling a customer something happened. */
@FunctionalInterface
public interface Notifier {
    void send(String recipient, String message);
}
