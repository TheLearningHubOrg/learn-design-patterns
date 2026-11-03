package org.thelearninghub.patterns.creational;

/**
 * Abstract Factory: one interface that creates a whole family of products. Client code
 * that receives a {@code UiKit} can never mix a light button with a dark checkbox.
 */
public interface UiKit {

    Button button(String label);

    Checkbox checkbox(String label, boolean checked);

    @FunctionalInterface
    interface Button {
        String render();
    }

    @FunctionalInterface
    interface Checkbox {
        String render();
    }

    enum Theme { LIGHT, DARK }

    static UiKit forTheme(Theme theme) {
        return switch (theme) {
            case LIGHT -> new LightKit();
            case DARK -> new DarkKit();
        };
    }

    final class LightKit implements UiKit {
        @Override
        public Button button(String label) {
            return () -> "<button class=\"btn-light\">" + label + "</button>";
        }

        @Override
        public Checkbox checkbox(String label, boolean checked) {
            return () -> "<label class=\"chk-light\">[" + (checked ? "x" : " ") + "] " + label + "</label>";
        }
    }

    final class DarkKit implements UiKit {
        @Override
        public Button button(String label) {
            return () -> "<button class=\"btn-dark\">" + label + "</button>";
        }

        @Override
        public Checkbox checkbox(String label, boolean checked) {
            return () -> "<label class=\"chk-dark\">[" + (checked ? "x" : " ") + "] " + label + "</label>";
        }
    }
}
