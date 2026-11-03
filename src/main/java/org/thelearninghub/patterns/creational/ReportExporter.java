package org.thelearninghub.patterns.creational;

import java.util.List;

/**
 * Factory Method. {@link #export} is the fixed algorithm; {@link #createFormatter} is the
 * creation step each subclass overrides to decide which product the algorithm uses.
 */
public abstract class ReportExporter {

    public interface RowFormatter {
        String header(List<String> cells);

        String row(List<String> cells);
    }

    protected abstract RowFormatter createFormatter();

    public final String export(List<List<String>> rows) {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("a report needs at least a header row");
        }
        RowFormatter formatter = createFormatter();
        StringBuilder out = new StringBuilder(formatter.header(rows.getFirst()));
        for (List<String> row : rows.subList(1, rows.size())) {
            out.append(formatter.row(row));
        }
        return out.toString();
    }

    public static final class CsvExporter extends ReportExporter {
        @Override
        protected RowFormatter createFormatter() {
            return new RowFormatter() {
                @Override
                public String header(List<String> cells) {
                    return row(cells);
                }

                @Override
                public String row(List<String> cells) {
                    return String.join(",", cells) + "\n";
                }
            };
        }
    }

    public static final class MarkdownExporter extends ReportExporter {
        @Override
        protected RowFormatter createFormatter() {
            return new RowFormatter() {
                @Override
                public String header(List<String> cells) {
                    return row(cells) + "|" + "---|".repeat(cells.size()) + "\n";
                }

                @Override
                public String row(List<String> cells) {
                    return "| " + String.join(" | ", cells) + " |\n";
                }
            };
        }
    }
}
