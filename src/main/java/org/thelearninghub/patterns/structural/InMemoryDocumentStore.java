package org.thelearninghub.patterns.structural;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/** The real subject. It knows nothing about security. */
public final class InMemoryDocumentStore implements DocumentStore {

    private final Map<String, String> documents = new ConcurrentHashMap<>();

    public InMemoryDocumentStore put(String id, String text) {
        documents.put(id, text);
        return this;
    }

    @Override
    public String read(String id) {
        String text = documents.get(id);
        if (text == null) {
            throw new NoSuchElementException("No document " + id);
        }
        return text;
    }

    @Override
    public void delete(String id) {
        documents.remove(id);
    }

    public boolean contains(String id) {
        return documents.containsKey(id);
    }
}
