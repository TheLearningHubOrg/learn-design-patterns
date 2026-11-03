package org.thelearninghub.patterns.creational;

/**
 * Closed family of notifications plus a static factory. Callers name a channel; the
 * factory picks the concrete type. Because the interface is sealed and Channel is an enum,
 * the switch below is checked for exhaustiveness: adding a channel without a case fails
 * compilation.
 */
public sealed interface Notification permits Notification.Email, Notification.Sms, Notification.Push {

    String recipient();

    String text();

    String deliver();

    enum Channel { EMAIL, SMS, PUSH }

    static Notification of(Channel channel, String recipient, String text) {
        return switch (channel) {
            case EMAIL -> new Email(recipient, text);
            case SMS -> new Sms(recipient, text);
            case PUSH -> new Push(recipient, text);
        };
    }

    record Email(String recipient, String text) implements Notification {
        public Email {
            if (!recipient.contains("@")) {
                throw new IllegalArgumentException("Not an email address: " + recipient);
            }
        }

        @Override
        public String deliver() {
            return "EMAIL to " + recipient + ": " + text;
        }
    }

    record Sms(String recipient, String text) implements Notification {
        public static final int MAX_LENGTH = 160;

        public Sms {
            if (text.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "SMS text is " + text.length() + " chars, limit is " + MAX_LENGTH);
            }
        }

        @Override
        public String deliver() {
            return "SMS to " + recipient + ": " + text;
        }
    }

    record Push(String recipient, String text) implements Notification {
        @Override
        public String deliver() {
            return "PUSH to device " + recipient + ": " + text;
        }
    }
}
