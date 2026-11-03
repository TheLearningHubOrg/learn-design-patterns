package org.thelearninghub.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.thelearninghub.patterns.behavioral.DiscountStrategy;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.PaymentResult;
import org.thelearninghub.patterns.solid.Receipt;
import org.thelearninghub.patterns.structural.Alert;
import org.thelearninghub.patterns.structural.CachingDataService;
import org.thelearninghub.patterns.structural.CheckoutFacade;
import org.thelearninghub.patterns.structural.DataService;
import org.thelearninghub.patterns.structural.DirectoryNode;
import org.thelearninghub.patterns.structural.DocumentStore;
import org.thelearninghub.patterns.structural.FileNode;
import org.thelearninghub.patterns.structural.GlyphFactory;
import org.thelearninghub.patterns.structural.InMemoryDocumentStore;
import org.thelearninghub.patterns.structural.LegacyPaymentAdapter;
import org.thelearninghub.patterns.structural.LegacyXmlPaymentService;
import org.thelearninghub.patterns.structural.LoggingDataService;
import org.thelearninghub.patterns.structural.ProtectedDocumentStore;
import org.thelearninghub.patterns.structural.RecordingProxy;
import org.thelearninghub.patterns.structural.RemoteDataService;
import org.thelearninghub.patterns.structural.RetryingDataService;

class StructuralTest {

    @Test
    void adapterTranslatesCentsToLegacyXmlAndBack() {
        LegacyPaymentAdapter adapter = new LegacyPaymentAdapter(new LegacyXmlPaymentService());

        PaymentResult ok = adapter.charge("c-1", Money.ofCents(25_000));
        PaymentResult tooBig = adapter.charge("c-1", Money.ofCents(1_000_001));

        assertTrue(ok.approved());
        assertEquals("LEG-1", ok.reference());
        assertFalse(tooBig.approved());
        assertEquals("over-limit", tooBig.reference());
    }

    @Test
    void decoratorsStackInAnyOrder() {
        RemoteDataService remote = new RemoteDataService();
        List<String> log = new ArrayList<>();
        DataService service = new LoggingDataService(new CachingDataService(remote), log::add);

        assertEquals("value-of-a", service.fetch("a"));
        assertEquals("value-of-a", service.fetch("a"));

        assertEquals(1, remote.calls()); // the cache absorbed the second call
        assertEquals(List.of("fetch(a) start", "fetch(a) ok", "fetch(a) start", "fetch(a) ok"), log);
    }

    @Test
    void retryingDecoratorRecoversFromTransientFailures() {
        AtomicInteger attempts = new AtomicInteger();
        DataService flaky = key -> {
            if (attempts.incrementAndGet() < 3) {
                throw new IllegalStateException("timeout #" + attempts.get());
            }
            return "ok:" + key;
        };

        assertEquals("ok:k", new RetryingDataService(flaky, 3).fetch("k"));
        attempts.set(0);
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new RetryingDataService(flaky, 2).fetch("k"));
        assertEquals("timeout #2", e.getMessage());
    }

    @Test
    void protectionProxyChecksRolesOnEveryCall() {
        InMemoryDocumentStore real = new InMemoryDocumentStore().put("d-1", "quarterly plan");
        AtomicReference<Set<String>> roles = new AtomicReference<>(Set.of("reader"));
        DocumentStore store = new ProtectedDocumentStore(real, roles::get);

        assertEquals("quarterly plan", store.read("d-1"));
        assertThrows(ProtectedDocumentStore.AccessDeniedException.class, () -> store.delete("d-1"));
        roles.set(Set.of("admin"));
        store.delete("d-1");
        assertFalse(real.contains("d-1"));
    }

    @Test
    void dynamicProxyRecordsCallsAndRethrowsTheRealException() {
        List<String> log = new ArrayList<>();
        DocumentStore proxy = RecordingProxy.wrap(DocumentStore.class, new InMemoryDocumentStore(), log);

        assertThrows(NoSuchElementException.class, () -> proxy.read("missing"));
        assertEquals(List.of("read[missing]"), log);
    }

    @Test
    void facadeRunsTheWholeCheckoutInOneCall() {
        List<String> notifications = new ArrayList<>();
        CheckoutFacade facade = CheckoutFacade.withLegacyPayments((to, msg) -> notifications.add(msg));

        Receipt receipt = facade.checkout("c-9", "vip@example.com", Money.ofCents(10_000), DiscountStrategy.forTier("vip"));

        assertEquals(Money.ofCents(8_000), receipt.net());   // 20% off
        assertEquals(Money.ofCents(640), receipt.tax());     // 8% of 80.00
        assertEquals(Money.ofCents(8_640), receipt.total());
        assertTrue(receipt.paid());
        assertEquals(List.of("Order ORD-1 confirmed: 86.40"), notifications);
    }

    @Test
    void compositeTreatsFilesAndDirectoriesUniformly() {
        DirectoryNode root = new DirectoryNode("workspace")
                .add(new FileNode("README.md", 1_200))
                .add(new DirectoryNode("src").add(new FileNode("App.java", 450)).add(new FileNode("app.css", 300)));

        assertEquals(1_950, root.size());
        assertEquals("""
                workspace/ (1950 B)
                  README.md (1200 B)
                  src/ (750 B)
                    App.java (450 B)
                    app.css (300 B)
                """, root.render());
    }

    @Test
    void flyweightSharesIntrinsicState() {
        GlyphFactory glyphs = new GlyphFactory();
        String text = "banana";
        List<String> drawn = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            drawn.add(glyphs.glyph(text.charAt(i), "Inter", 12).drawAt(i * 8, 0));
        }

        assertEquals(3, glyphs.pooled()); // b, a, n
        assertSame(glyphs.glyph('a', "Inter", 12), glyphs.glyph('a', "Inter", 12));
        assertEquals("n@16,0 [Inter 12pt]", drawn.get(2));
    }

    @Test
    void bridgeCombinesAnyAlertWithAnySender() {
        List<String> out = new ArrayList<>();
        Alert.MessageSender collecting = (channel, text) -> out.add(channel + ":" + text);

        new Alert.Urgent(collecting).raise("disk 95% full");

        assertEquals(List.of("pager:URGENT: disk 95% full", "info:escalated: disk 95% full"), out);
    }
}
