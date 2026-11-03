package org.thelearninghub.patterns.structural;

/** Leaf: does the work itself and has no children. */
public record FileNode(String name, long size) implements FsNode {

    public FileNode {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0, was " + size);
        }
    }

    @Override
    public void render(String indent, StringBuilder out) {
        out.append(indent).append(name).append(" (").append(size).append(" B)\n");
    }
}
