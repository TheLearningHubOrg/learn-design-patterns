package org.thelearninghub.patterns.creational;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prototype via a copy constructor instead of {@code Cloneable}. Each copy gets its own
 * list and map, so editing a copy never leaks into the original. That is a deep enough
 * copy here because the elements are immutable strings.
 */
public final class DocumentTemplate {

    private final String title;
    private final List<String> sections;
    private final Map<String, String> metadata;

    public DocumentTemplate(String title, List<String> sections, Map<String, String> metadata) {
        this.title = title;
        this.sections = new ArrayList<>(sections);
        this.metadata = new LinkedHashMap<>(metadata);
    }

    private DocumentTemplate(DocumentTemplate source, String newTitle) {
        this(newTitle, source.sections, source.metadata);
    }

    public DocumentTemplate copy(String newTitle) {
        return new DocumentTemplate(this, newTitle);
    }

    public void addSection(String section) {
        sections.add(section);
    }

    public void tag(String key, String value) {
        metadata.put(key, value);
    }

    public String title() {
        return title;
    }

    public List<String> sections() {
        return List.copyOf(sections);
    }

    public Map<String, String> metadata() {
        return Map.copyOf(metadata);
    }
}
