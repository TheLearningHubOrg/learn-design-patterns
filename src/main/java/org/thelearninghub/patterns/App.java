package org.thelearninghub.patterns;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import org.thelearninghub.patterns.creational.LazyConfig;
import org.thelearninghub.patterns.web.RequestContext;
import org.thelearninghub.patterns.web.Router;
import org.thelearninghub.patterns.structural.CheckoutFacade;

/**
 * Composition root: the one place that picks concrete classes and wires them together.
 * Everything below it receives its collaborators through constructors.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) throws IOException {
        int port = LazyConfig.get().intValue("PORT", 8080);
        HttpServer server = start(port);
        System.out.printf("learn-design-patterns listening on 0.0.0.0:%d%n", server.getAddress().getPort());
    }

    /** Starts the server; pass port 0 to let the OS pick a free port (used by the tests). */
    public static HttpServer start(int port) throws IOException {
        CheckoutFacade checkout = CheckoutFacade.withLegacyPayments(
                (recipient, message) -> System.out.printf("notify %s: %s%n", recipient, message));
        Router router = new Router(checkout);

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", exchange -> respond(exchange, router));
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        return server;
    }

    private static void respond(HttpExchange exchange, Router router) throws IOException {
        try {
            RequestContext ctx = router.handle(new RequestContext(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getPath(),
                    exchange.getRequestURI().getRawQuery()));
            byte[] bytes = ctx.body().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(ctx.status(), bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        } finally {
            exchange.close();
        }
    }
}
