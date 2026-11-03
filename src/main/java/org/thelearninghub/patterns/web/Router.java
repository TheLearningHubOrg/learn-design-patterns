package org.thelearninghub.patterns.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.thelearninghub.patterns.behavioral.DiscountStrategy;
import org.thelearninghub.patterns.behavioral.MiddlewareChain;
import org.thelearninghub.patterns.catalog.PatternCatalog;
import org.thelearninghub.patterns.creational.MetricsRegistry;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.Receipt;
import org.thelearninghub.patterns.structural.CheckoutFacade;

/**
 * Builds the request pipeline: metrics, then a method guard, then routing. Each step is a
 * middleware in a {@link MiddlewareChain}, so adding auth or rate limiting is one more
 * {@code use(...)} call.
 */
public final class Router {

    public static final String REQUEST_COUNTER = "http.requests";

    private final MiddlewareChain<RequestContext> chain = new MiddlewareChain<>();
    private final CheckoutFacade checkout;

    public Router(CheckoutFacade checkout) {
        this.checkout = checkout;
        chain.use((ctx, next) -> {
            MetricsRegistry.INSTANCE.increment(REQUEST_COUNTER);
            next.run();
        });
        chain.use((ctx, next) -> {
            if (!ctx.method().equals("GET")) {
                ctx.respond(405, Json.write(Map.of("error", "method not allowed")));
                return; // stop the chain
            }
            next.run();
        });
        chain.use((ctx, next) -> route(ctx));
    }

    public RequestContext handle(RequestContext ctx) {
        try {
            return chain.execute(ctx);
        } catch (IllegalArgumentException e) {
            ctx.respond(400, Json.write(Map.of("error", e.getMessage())));
            return ctx;
        }
    }

    private void route(RequestContext ctx) {
        switch (ctx.path()) {
            case "/health" -> ctx.respond(200, Json.write(Map.of(
                    "status", "UP",
                    "requests", MetricsRegistry.INSTANCE.count(REQUEST_COUNTER))));
            case "/api/patterns" -> ctx.respond(200, Json.write(patterns(ctx.param("category", ""))));
            case "/api/checkout" -> ctx.respond(200, Json.write(checkout(ctx)));
            default -> {
                // leave the 404 default in place
            }
        }
    }

    private static List<Map<String, Object>> patterns(String category) {
        List<PatternCatalog.Entry> entries =
                category.isBlank() ? PatternCatalog.all() : PatternCatalog.byCategory(category);
        return entries.stream().map(e -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", e.name());
            row.put("category", e.category().name());
            row.put("stage", e.stage());
            row.put("intent", e.intent());
            return row;
        }).toList();
    }

    private Map<String, Object> checkout(RequestContext ctx) {
        long cents;
        try {
            cents = Long.parseLong(ctx.param("amountCents", "0"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("amountCents must be a whole number of cents", e);
        }
        Receipt receipt = checkout.checkout(
                ctx.param("customer", "guest"),
                ctx.param("email", "guest@example.com"),
                Money.ofCents(cents),
                DiscountStrategy.forTier(ctx.param("tier", "regular")));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orderId", receipt.orderId());
        body.put("net", receipt.net().toString());
        body.put("tax", receipt.tax().toString());
        body.put("total", receipt.total().toString());
        body.put("paid", receipt.paid());
        body.put("reference", receipt.reference());
        return body;
    }
}
