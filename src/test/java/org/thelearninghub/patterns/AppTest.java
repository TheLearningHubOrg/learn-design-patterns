package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppTest {

    private static HttpServer server;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    @BeforeAll
    static void startServer() throws IOException {
        server = App.start(0);
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    private static HttpResponse<String> send(String method, String path) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + path);
        HttpRequest request = HttpRequest.newBuilder(uri).method(method, HttpRequest.BodyPublishers.noBody()).build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void healthEndpointAnswers() throws Exception {
        HttpResponse<String> response = send("GET", "/health");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""), response.body());
    }

    @Test
    void catalogFiltersByCategory() throws Exception {
        HttpResponse<String> response = send("GET", "/api/patterns?category=structural");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"name\":\"Adapter\""));
        assertTrue(!response.body().contains("\"name\":\"Builder\""));
    }

    @Test
    void checkoutRunsThroughTheFacade() throws Exception {
        HttpResponse<String> response = send("GET", "/api/checkout?amountCents=10000&tier=vip&email=v%40example.com");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"total\":\"86.40\""), response.body());
    }

    @Test
    void badInputIsA400AndOtherMethodsAre405() throws Exception {
        assertEquals(400, send("GET", "/api/checkout?amountCents=abc").statusCode());
        assertEquals(400, send("GET", "/api/checkout?tier=gold").statusCode());
        assertEquals(405, send("POST", "/health").statusCode());
        assertEquals(404, send("GET", "/nope").statusCode());
    }
}
