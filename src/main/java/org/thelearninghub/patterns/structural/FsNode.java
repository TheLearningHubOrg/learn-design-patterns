package org.thelearninghub.patterns.structural;

/** Composite component: files (leaves) and directories (composites) share one interface. */
public sealed interface FsNode permits FileNode, DirectoryNode {

    String name();

    long size();

    void render(String indent, StringBuilder out);

    default String render() {
        StringBuilder out = new StringBuilder();
        render("", out);
        return out.toString();
    }
}
