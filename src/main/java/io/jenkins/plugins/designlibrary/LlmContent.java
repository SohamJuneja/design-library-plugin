package io.jenkins.plugins.designlibrary;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
class LlmContent {

    private static final Pattern CODE_FILE_PATTERN = Pattern.compile("file=\"([^\"]+)\"");

    private LlmContent() {}

    static String generateIndex(String baseUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Jenkins Design Library\n\n");
        sb.append("> A reference library of UI components and patterns ");
        sb.append("for building Jenkins plugin interfaces.\n\n");

        for (Map.Entry<Category, List<UISample>> entry : UISample.getGrouped().entrySet()) {
            sb.append("## ").append(entry.getKey().getDisplayName()).append('\n');
            for (UISample sample : entry.getValue()) {
                sb.append("- [").append(sample.getDisplayName()).append("](");
                sb.append(baseUrl).append(sample.getUrlName()).append(".md): ");
                sb.append(sample.getDescription()).append('\n');
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    static String generateAll(ServletContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Jenkins Design Library\n\n");
        sb.append("> A reference library of UI components and patterns ");
        sb.append("for building Jenkins plugin interfaces.\n\n");

        for (Map.Entry<Category, List<UISample>> entry : UISample.getGrouped().entrySet()) {
            sb.append("## ").append(entry.getKey().getDisplayName()).append("\n\n");
            for (UISample sample : entry.getValue()) {
                sb.append(generateComponentMarkdown(sample, context));
                sb.append("\n---\n\n");
            }
        }

        return sb.toString();
    }

    static String generateComponentMarkdown(UISample sample, ServletContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(sample.getDisplayName()).append("\n\n");
        sb.append("> ").append(sample.getDescription()).append("\n\n");
        sb.append("**Category:** ")
                .append(sample.getCategory().getDisplayName())
                .append('\n');

        if (sample.getSince() != null) {
            sb.append("**Since:** ").append(sample.getSince()).append('\n');
        }
        sb.append('\n');

        String componentName = sample.getClass().getSimpleName();
        List<String> snippetFiles = findSnippetReferences(sample);

        for (String filename : snippetFiles) {
            String path = "/" + componentName + "/" + filename;
            String content = readResource(context, path);

            if (content != null && !content.isBlank()) {
                String label = filename.replaceFirst("\\.[^.]+$", "");
                String language = filename.endsWith(".js") ? "javascript" : "xml";
                sb.append("### ").append(label).append("\n\n");
                sb.append("```").append(language).append('\n');
                sb.append(content.strip()).append('\n');
                sb.append("```\n\n");
            }
        }

        return sb.toString();
    }

    private static List<String> findSnippetReferences(UISample sample) {
        List<String> files = new ArrayList<>();
        String jellyPath = sample.getClass().getName().replace('.', '/') + "/index.jelly";

        try (InputStream is = sample.getClass().getClassLoader().getResourceAsStream(jellyPath)) {
            if (is == null) {
                return files;
            }
            String jelly = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Matcher matcher = CODE_FILE_PATTERN.matcher(jelly);
            while (matcher.find()) {
                String file = matcher.group(1);
                if (!files.contains(file)) {
                    files.add(file);
                }
            }
        } catch (IOException e) {
            // skip
        }

        return files;
    }

    private static String readResource(ServletContext context, String path) {
        try (InputStream is = context.getResourceAsStream(path)) {
            if (is == null) {
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
