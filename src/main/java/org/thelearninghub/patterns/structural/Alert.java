package org.thelearninghub.patterns.structural;

import java.util.Objects;

/**
 * Bridge. The abstraction (what kind of alert) and the implementation (how a message
 * travels) vary independently: 2 alert kinds x N senders needs 2 + N classes, not 2 x N.
 */
public abstract class Alert {

    /** The implementor side of the bridge. */
    @FunctionalInterface
    public interface MessageSender {
        void send(String channel, String text);
    }

    protected final MessageSender sender;

    protected Alert(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender");
    }

    public abstract void raise(String text);

    public static final class Info extends Alert {
        public Info(MessageSender sender) {
            super(sender);
        }

        @Override
        public void raise(String text) {
            sender.send("info", text);
        }
    }

    public static final class Urgent extends Alert {
        public Urgent(MessageSender sender) {
            super(sender);
        }

        @Override
        public void raise(String text) {
            sender.send("pager", "URGENT: " + text);
            sender.send("info", "escalated: " + text);
        }
    }
}
