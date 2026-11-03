package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.thelearninghub.patterns.creational.DocumentTemplate;
import org.thelearninghub.patterns.creational.HttpRequest;
import org.thelearninghub.patterns.creational.LazyConfig;
import org.thelearninghub.patterns.creational.MetricsRegistry;
import org.thelearninghub.patterns.creational.Notification;
import org.thelearninghub.patterns.creational.ReportExporter;
import org.thelearninghub.patterns.creational.UiKit;

class CreationalTest {

    @AfterEach
    void resetGlobalState() {
        MetricsRegistry.INSTANCE.reset();
    }

    @Test
    void enumSingletonCountsCorrectlyUnderConcurrency() throws Exception {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000).forEach(i -> pool.submit(() -> MetricsRegistry.INSTANCE.increment("hits")));
        } // close() waits for every task
        assertEquals(1_000, MetricsRegistry.INSTANCE.count("hits"));
        assertSame(MetricsRegistry.INSTANCE, MetricsRegistry.valueOf("INSTANCE"));
    }

    @Test
    void holderSingletonReturnsOneInstance() {
        assertSame(LazyConfig.get(), LazyConfig.get());
        LazyConfig config = LazyConfig.of(Map.of("PORT", "9090", "BAD", "x"));
        assertEquals(9090, config.intValue("PORT", 8080));
        assertEquals(8080, config.intValue("MISSING", 8080));
        assertThrows(IllegalArgumentException.class, () -> config.intValue("BAD", 1));
    }

    @Test
    void builderAppliesDefaultsAndValidatesAcrossFields() {
        HttpRequest request = HttpRequest.builder("https://api.example.com/courses")
                .method("post")
                .header("Content-Type", "application/json")
                .body("{\"title\":\"Patterns\"}")
                .build();

        assertEquals("POST", request.method());
        assertEquals(Duration.ofSeconds(5), request.timeout());
        assertThrows(UnsupportedOperationException.class, () -> request.headers().put("X", "y"));
        assertThrows(IllegalStateException.class,
                () -> HttpRequest.builder("https://api.example.com").body("oops").build());
        assertThrows(IllegalArgumentException.class, () -> HttpRequest.builder("https://x.test").method("TRACE"));
    }

    @Test
    void staticFactoryPicksTheConcreteType() {
        Notification n = Notification.of(Notification.Channel.SMS, "+15550100", "Your code is 1234");
        assertInstanceOf(Notification.Sms.class, n);
        assertEquals("SMS to +15550100: Your code is 1234", n.deliver());
        assertThrows(IllegalArgumentException.class,
                () -> Notification.of(Notification.Channel.EMAIL, "not-an-address", "hi"));
    }

    @Test
    void factoryMethodLetsSubclassesChooseTheFormatter() {
        List<List<String>> rows = List.of(List.of("pattern", "stage"), List.of("Builder", "2"));
        assertEquals("pattern,stage\nBuilder,2\n", new ReportExporter.CsvExporter().export(rows));
        assertEquals("| pattern | stage |\n|---|---|\n| Builder | 2 |\n",
                new ReportExporter.MarkdownExporter().export(rows));
    }

    @Test
    void abstractFactoryKeepsAFamilyConsistent() {
        UiKit dark = UiKit.forTheme(UiKit.Theme.DARK);
        assertEquals("<button class=\"btn-dark\">Save</button>", dark.button("Save").render());
        assertEquals("<label class=\"chk-dark\">[x] Remember me</label>", dark.checkbox("Remember me", true).render());
    }

    @Test
    void prototypeCopiesAreIndependent() {
        DocumentTemplate base = new DocumentTemplate("RFC", List.of("Context", "Decision"), Map.of("owner", "platform"));
        DocumentTemplate copy = base.copy("RFC-42");
        copy.addSection("Consequences");
        copy.tag("owner", "payments");

        assertEquals(List.of("Context", "Decision"), base.sections());
        assertEquals("platform", base.metadata().get("owner"));
        assertEquals(List.of("Context", "Decision", "Consequences"), copy.sections());
    }
}
