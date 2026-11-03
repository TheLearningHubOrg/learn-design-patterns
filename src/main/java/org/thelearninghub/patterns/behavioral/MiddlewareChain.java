package org.thelearninghub.patterns.behavioral;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Chain of Responsibility in the middleware style: each handler gets the context and a
 * {@code next} callback, and decides whether the rest of the chain runs.
 */
public final class MiddlewareChain<C> {

    @FunctionalInterface
    public interface Middleware<C> {
        void handle(C context, Runnable next);
    }

    private final List<Middleware<C>> middlewares = new ArrayList<>();

    public MiddlewareChain<C> use(Middleware<C> middleware) {
        middlewares.add(Objects.requireNonNull(middleware, "middleware"));
        return this;
    }

    public C execute(C context) {
        dispatch(0, context);
        return context;
    }

    private void dispatch(int index, C context) {
        if (index < middlewares.size()) {
            middlewares.get(index).handle(context, () -> dispatch(index + 1, context));
        }
    }
}
