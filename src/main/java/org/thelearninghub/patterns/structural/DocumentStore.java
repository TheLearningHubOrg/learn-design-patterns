package org.thelearninghub.patterns.structural;

/** Subject interface shared by the real store and its proxies. */
public interface DocumentStore {

    String read(String id);

    void delete(String id);
}
