package org.thelearninghub.patterns.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Mutable context the middleware chain passes along: request in, status and body out. */
public final class RequestContext {

    private final String method;
    private final String path;
    private final Map<String, String> query;
    private int status = 404;
    private String body = "{\"error\":\"not found\"}";

    public RequestContext(String method, String path, String rawQuery) {
        this.method = method;
        this.path = path;
        this.query = parse(rawQuery);
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String param(String name, String fallback) {
        return query.getOrDefault(name, fallback);
    }

    public void respond(int newStatus, String json) {
        this.status = newStatus;
        this.body = json;
    }

    public int status() {
        return status;
    }

    public String body() {
        return body;
    }

    private static Map<String, String> parse(String rawQuery) {
        Map<String, String> params = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return params;
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            String key = eq < 0 ? pair : pair.substring(0, eq);
            String value = eq < 0 ? "" : pair.substring(eq + 1);
            params.put(URLDecoder.decode(key, StandardCharsets.UTF_8), URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return params;
    }
}
