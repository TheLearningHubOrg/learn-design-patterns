package org.thelearninghub.patterns.web;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

/** Just enough JSON writing for the demo API, so the starter has no runtime dependencies. */
public final class Json {

    private Json() {
    }

    public static String write(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String s) {
            return quote(s);
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringJoiner out = new StringJoiner(",", "{", "}");
            map.forEach((k, v) -> out.add(quote(String.valueOf(k)) + ":" + write(v)));
            return out.toString();
        }
        if (value instanceof Collection<?> items) {
            StringJoiner out = new StringJoiner(",", "[", "]");
            items.forEach(item -> out.add(write(item)));
            return out.toString();
        }
        return quote(value.toString());
    }

    static String quote(String raw) {
        StringBuilder out = new StringBuilder("\"");
        for (char c : raw.toCharArray()) {
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.append('"').toString();
    }
}
