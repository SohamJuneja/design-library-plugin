package io.jenkins.plugins.designlibrary;

/**
 * Renders the markdown-only representation of the Design Library sample components.
 */
public final class MarkdownComponentRenderer {

    public String layout(String displayName, String description, String category, String since, String body) {
        StringBuilder markdown = new StringBuilder();
        appendBlock(markdown, displayName == null || displayName.isBlank() ? null : "# " + displayName);
        appendBlock(markdown, quote(description));
        appendBlock(markdown, metadata(category, since));
        appendBlock(markdown, normalizeBlock(body));
        return markdown.toString();
    }

    public String section(String title, String description, String body) {
        StringBuilder markdown = new StringBuilder();
        appendBlock(markdown, title == null || title.isBlank() ? null : "## " + normalizeInline(title));
        appendBlock(markdown, description);
        appendBlock(markdown, normalizeBlock(body));
        return block(markdown.toString());
    }

    public String group(String body) {
        return block(normalizeBlock(body));
    }

    public String code(String language, String code) {
        String normalizedLanguage = language == null || language.isBlank() ? "xml" : language;
        String normalizedCode = normalizeCode(code);
        return block("```" + normalizedLanguage + "\n" + normalizedCode + "\n```");
    }

    public String codePanes(String body) {
        return block(normalizeBlock(body));
    }

    public String codePane(String title, String body) {
        StringBuilder markdown = new StringBuilder();
        appendBlock(markdown, title == null || title.isBlank() ? null : "### " + normalizeInline(title));
        appendBlock(markdown, normalizeBlock(body));
        return block(markdown.toString());
    }

    public String paragraph(String body) {
        String normalizedBody = normalizeInline(body);
        return normalizedBody.isEmpty() ? "" : block(normalizedBody);
    }

    public String list(String body) {
        return block(normalizeBlock(body));
    }

    public String listItem(String body) {
        String normalizedBody = normalizeInline(body);
        return normalizedBody.isEmpty() ? "" : "- " + normalizedBody + "\n";
    }

    private String quote(String text) {
        String normalized = normalizeInline(text);
        return normalized.isEmpty() ? "" : "> " + normalized;
    }

    private String metadata(String category, String since) {
        StringBuilder metadata = new StringBuilder();
        String normalizedCategory = normalizeInline(category);
        String normalizedSince = normalizeInline(since);
        if (!normalizedCategory.isEmpty()) {
            metadata.append("**Category:** ").append(normalizedCategory);
        }
        if (!normalizedSince.isEmpty()) {
            if (!metadata.isEmpty()) {
                metadata.append('\n');
            }
            metadata.append("**Since:** ").append(normalizedSince);
        }
        return metadata.toString();
    }

    private String block(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.stripTrailing() + "\n\n";
    }

    private void appendBlock(StringBuilder markdown, String block) {
        if (block == null || block.isBlank()) {
            return;
        }
        if (!markdown.isEmpty()) {
            markdown.append('\n').append('\n');
        }
        markdown.append(block.stripTrailing());
    }

    private String normalizeCode(String text) {
        String normalized = normalizeNewlines(text);
        return normalized.replaceFirst("\\n+$", "");
    }

    private String normalizeInline(String text) {
        String normalized = normalizeNewlines(text);
        normalized = normalized.replaceAll("(?m)^[ \\t]+", "");
        normalized = normalized.replaceAll("[ \\t]*\\n[ \\t]*", " ");
        normalized = normalized.replaceAll(" {2,}", " ");
        return normalized.trim();
    }

    private String normalizeBlock(String text) {
        String normalized = normalizeNewlines(text);
        normalized = normalized.replaceAll("(?m)^[ \\t]+$", "");
        normalized = normalized.replaceAll("(?m)^[ \\t]+(?=(?:#{1,6}\\s|```|> |- |\\d+\\. ))", "");
        normalized = normalized.replaceFirst("^(?:\\n)+", "");
        return normalized.replaceFirst("(?:\\n)+$", "");
    }

    private String normalizeNewlines(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
