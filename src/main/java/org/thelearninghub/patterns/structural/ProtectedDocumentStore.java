package org.thelearninghub.patterns.structural;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Protection proxy: same interface as the real store, checks the caller's roles first.
 * The roles come from a supplier so the proxy reads the current caller on every call.
 */
public final class ProtectedDocumentStore implements DocumentStore {

    private final DocumentStore target;
    private final Supplier<Set<String>> currentRoles;

    public ProtectedDocumentStore(DocumentStore target, Supplier<Set<String>> currentRoles) {
        this.target = Objects.requireNonNull(target, "target");
        this.currentRoles = Objects.requireNonNull(currentRoles, "currentRoles");
    }

    @Override
    public String read(String id) {
        require("reader", "admin");
        return target.read(id);
    }

    @Override
    public void delete(String id) {
        require("admin");
        target.delete(id);
    }

    private void require(String... anyOf) {
        Set<String> roles = currentRoles.get();
        for (String role : anyOf) {
            if (roles.contains(role)) {
                return;
            }
        }
        throw new AccessDeniedException("requires one of " + String.join(", ", anyOf) + "; caller has " + roles);
    }

    /** Unchecked so it passes through the {@code DocumentStore} interface untouched. */
    public static final class AccessDeniedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
