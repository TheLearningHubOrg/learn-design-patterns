package org.thelearninghub.patterns.structural;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thelearninghub.patterns.solid.Money;
import org.thelearninghub.patterns.solid.PaymentGateway;
import org.thelearninghub.patterns.solid.PaymentResult;

/**
 * Object adapter: implements the interface our code expects (target) and holds the
 * legacy service (adaptee). All translation, in both directions, lives in this class.
 */
public final class LegacyPaymentAdapter implements PaymentGateway {

    private static final Pattern STATUS = Pattern.compile("status=\"(\\w+)\"");
    private static final Pattern REF = Pattern.compile("ref=\"([^\"]+)\"");
    private static final Pattern REASON = Pattern.compile("reason=\"([^\"]+)\"");

    private final LegacyXmlPaymentService legacy;

    public LegacyPaymentAdapter(LegacyXmlPaymentService legacy) {
        this.legacy = Objects.requireNonNull(legacy, "legacy");
    }

    @Override
    public PaymentResult charge(String customerId, Money amount) {
        String request = "<payment account=\"" + escape(customerId) + "\" amount=\"" + amount
                + "\" currency=\"USD\"/>";
        String reply = legacy.submit(request);
        String status = find(STATUS, reply).orElse("ERROR");
        if (status.equals("OK")) {
            return PaymentResult.approved(find(REF, reply).orElseThrow());
        }
        return PaymentResult.declined(find(REASON, reply).orElse(status.toLowerCase(java.util.Locale.ROOT)));
    }

    private static Optional<String> find(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    private static String escape(String raw) {
        return raw.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
