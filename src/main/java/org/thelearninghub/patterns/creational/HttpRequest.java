package org.thelearninghub.patterns.creational;

import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Immutable request value. The only way to make one with defaults is the {@link Builder}. */
public record HttpRequest(String method, URI uri, Map<String, String> headers, String body, Duration timeout) {

    public HttpRequest {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(uri, "uri");
        headers = Map.copyOf(headers);
        Objects.requireNonNull(body, "body");
        Objects.requireNonNull(timeout, "timeout");
    }

    public static Builder builder(String uri) {
        return new Builder(URI.create(uri));
    }

    public static final class Builder {

        private static final Set<String> METHODS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

        private final URI uri;
        private String method = "GET";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private String body = "";
        private Duration timeout = Duration.ofSeconds(5);

        private Builder(URI uri) {
            this.uri = uri;
        }

        public Builder method(String value) {
            String upper = value.toUpperCase(Locale.ROOT);
            if (!METHODS.contains(upper)) {
                throw new IllegalArgumentException("Unsupported method: " + value);
            }
            this.method = upper;
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder body(String value) {
            this.body = Objects.requireNonNull(value, "body");
            return this;
        }

        public Builder timeout(Duration value) {
            if (value.isNegative() || value.isZero()) {
                throw new IllegalArgumentException("timeout must be positive, was " + value);
            }
            this.timeout = value;
            return this;
        }

        /** Cross-field rules live here, where every field is known. */
        public HttpRequest build() {
            if (!body.isEmpty() && (method.equals("GET") || method.equals("DELETE"))) {
                throw new IllegalStateException(method + " requests must not carry a body");
            }
            return new HttpRequest(method, uri, headers, body, timeout);
        }
    }
}
