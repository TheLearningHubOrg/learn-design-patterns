package org.thelearninghub.patterns.structural;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The adaptee: an old vendor API we cannot change. It speaks XML strings and decimal
 * amounts, while the rest of the system speaks {@code PaymentGateway} and cents.
 */
public final class LegacyXmlPaymentService {

    private static final Pattern AMOUNT = Pattern.compile("amount=\"([0-9]+\\.[0-9]{2})\"");
    private static final BigDecimal LIMIT = new BigDecimal("10000.00");

    private int sequence;

    /** Accepts {@code <payment account="..." amount="12.34" currency="USD"/>}. */
    public synchronized String submit(String xml) {
        Matcher amount = AMOUNT.matcher(xml);
        if (!xml.startsWith("<payment ") || !amount.find()) {
            return "<result status=\"ERROR\" reason=\"malformed\"/>";
        }
        if (new BigDecimal(amount.group(1)).compareTo(LIMIT) > 0) {
            return "<result status=\"DECLINED\" reason=\"over-limit\"/>";
        }
        sequence++;
        return "<result status=\"OK\" ref=\"LEG-" + sequence + "\"/>";
    }
}
