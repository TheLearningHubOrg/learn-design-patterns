package org.thelearninghub.patterns.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Composite: holds children and delegates every operation to them recursively. */
public final class DirectoryNode implements FsNode {

    private final String name;
    private final List<FsNode> children = new ArrayList<>();

    public DirectoryNode(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public DirectoryNode add(FsNode child) {
        if (child == this) {
            throw new IllegalArgumentException("a directory cannot contain itself");
        }
        children.add(Objects.requireNonNull(child, "child"));
        return this;
    }

    public List<FsNode> children() {
        return List.copyOf(children);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public long size() {
        return children.stream().mapToLong(FsNode::size).sum();
    }

    @Override
    public void render(String indent, StringBuilder out) {
        out.append(indent).append(name).append("/ (").append(size()).append(" B)\n");
        for (FsNode child : children) {
            child.render(indent + "  ", out);
        }
    }
}
