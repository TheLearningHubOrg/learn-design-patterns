package org.thelearninghub.patterns.structural;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight factory. A glyph's symbol, font and size are intrinsic and shared; the
 * position is extrinsic and passed in on every draw call, so one object serves every
 * occurrence of the same character in a document.
 */
public final class GlyphFactory {

    public record Glyph(char symbol, String font, int pointSize) {
        public String drawAt(int x, int y) {
            return symbol + "@" + x + "," + y + " [" + font + " " + pointSize + "pt]";
        }
    }

    private record Key(char symbol, String font, int pointSize) {
    }

    private final Map<Key, Glyph> pool = new ConcurrentHashMap<>();

    public Glyph glyph(char symbol, String font, int pointSize) {
        return pool.computeIfAbsent(new Key(symbol, font, pointSize), k -> new Glyph(k.symbol(), k.font(), k.pointSize()));
    }

    public int pooled() {
        return pool.size();
    }
}
