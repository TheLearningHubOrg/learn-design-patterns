package org.thelearninghub.patterns.solid;

import java.util.Objects;

public record Order(String id, String customerId, String email, Money net) {

    public Order {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customerId, "customerId");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(net, "net");
    }
}
