package org.thelearninghub.patterns.structural;

/** The component interface every decorator implements and wraps. */
@FunctionalInterface
public interface DataService {
    String fetch(String key);
}
