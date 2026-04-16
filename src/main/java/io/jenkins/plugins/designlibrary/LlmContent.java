package io.jenkins.plugins.designlibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
class LlmContent {

    private static final Pattern CODE_FILE_PATTERN = Pattern.compile("file=\"([^\"]+)\"");
    private static final Pattern INLINE_CODE_ELEMENT_PATTERN = Pattern.compile("<s:code\\b(.*?)/>", Pattern.DOTALL);
    private static final Pattern ATTR_CODE_PATTERN = Pattern.compile("\\bcode=\"([^\"]*)\"");
    private static final Pattern ATTR_CALLBACK_PATTERN = Pattern.compile("\\bcallback=\"([^\"]*)\"");
    private static final Pattern DOS_DONTS_PATTERN =
            Pattern.compile("<s:dos-donts>(.*?)</s:dos-donts>", Pattern.DOTALL);
    private static final Pattern TR_PATTERN = Pattern.compile("<tr>(.*?)</tr>", Pattern.DOTALL);
    private static final Pattern TD_PATTERN = Pattern.compile("<td>(.*?)</td>", Pattern.DOTALL);
    private static final Pattern I18N_KEY_PATTERN = Pattern.compile("\\$\\{%([^}]+)\\}");

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

    static String generateAll(URL pluginResourceBase) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Jenkins Design Library\n\n");
        sb.append("> A reference library of UI components and patterns ");
        sb.append("for building Jenkins plugin interfaces.\n\n");

        for (Map.Entry<Category, List<UISample>> entry : UISample.getGrouped().entrySet()) {
            sb.append("## ").append(entry.getKey().getDisplayName()).append("\n\n");
            for (UISample sample : entry.getValue()) {
                sb.append(generateComponentMarkdown(sample, pluginResourceBase));
                sb.append("\n---\n\n");
            }
        }

        return sb.toString();
    }

    static String generateComponentMarkdown(UISample sample, URL pluginResourceBase) {
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
        String jellyContent = loadJellyIndex(sample);
        List<String> snippetFiles = findSnippetReferences(jellyContent);

        for (String filename : snippetFiles) {
            String content = readResource(sample, componentName, filename, pluginResourceBase);

            if (content != null && !content.isBlank()) {
                String label = filename.replaceFirst("\\.[^.]+$", "");
                String language = inferLanguage(filename);
                sb.append("### ").append(label).append("\n\n");
                sb.append("```").append(language).append('\n');
                sb.append(content.strip()).append('\n');
                sb.append("```\n\n");
            }
        }

        for (String[] inline : findInlineSnippets(jellyContent)) {
            sb.append("### ").append(inline[0]).append("\n\n");
            sb.append("```").append(inline[1]).append('\n');
            sb.append(inline[2]).append('\n');
            sb.append("```\n\n");
        }

        if (jellyContent != null) {
            Properties props = loadProperties(sample);
            List<String[]> dosDonts = extractDosDonts(jellyContent, props);
            if (!dosDonts.isEmpty()) {
                sb.append("### Dos and Don'ts\n\n");
                sb.append("| Do | Don't |\n");
                sb.append("|---|---|\n");
                for (String[] pair : dosDonts) {
                    String doText = pair[0];
                    String dontText = pair.length > 1 ? pair[1] : "";
                    sb.append("| ")
                            .append(doText)
                            .append(" | ")
                            .append(dontText)
                            .append(" |\n");
                }
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    private static String loadJellyIndex(UISample sample) {
        String jellyPath = sample.getClass().getName().replace('.', '/') + "/index.jelly";
        try (InputStream is = sample.getClass().getClassLoader().getResourceAsStream(jellyPath)) {
            if (is == null) {
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private static List<String[]> findInlineSnippets(String jellyContent) {
        List<String[]> result = new ArrayList<>();
        if (jellyContent == null) {
            return result;
        }
        Matcher m = INLINE_CODE_ELEMENT_PATTERN.matcher(jellyContent);
        int counter = 1;
        while (m.find()) {
            String attrs = m.group(1);
            if (CODE_FILE_PATTERN.matcher(attrs).find()) {
                continue; // file= snippets are handled separately
            }
            Matcher codeMatcher = ATTR_CODE_PATTERN.matcher(attrs);
            if (!codeMatcher.find()) {
                continue;
            }
            String code = codeMatcher.group(1);
            String label;
            Matcher callbackMatcher = ATTR_CALLBACK_PATTERN.matcher(attrs);
            if (callbackMatcher.find()) {
                label = callbackMatcher.group(1);
            } else {
                label = "snippet" + counter++;
            }
            result.add(new String[] {label, "javascript", code});
        }
        return result;
    }

    private static Properties loadProperties(UISample sample) {
        String propsPath = sample.getClass().getName().replace('.', '/') + "/index.properties";
        Properties props = new Properties();
        try (InputStream is = sample.getClass().getClassLoader().getResourceAsStream(propsPath)) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            }
        } catch (IOException e) {
            // skip
        }
        return props;
    }

    private static List<String[]> extractDosDonts(String jellyContent, Properties props) {
        List<String[]> pairs = new ArrayList<>();
        Matcher dosDontsMatcher = DOS_DONTS_PATTERN.matcher(jellyContent);
        while (dosDontsMatcher.find()) {
            String block = dosDontsMatcher.group(1);
            Matcher trMatcher = TR_PATTERN.matcher(block);
            while (trMatcher.find()) {
                String row = trMatcher.group(1);
                List<String> cells = new ArrayList<>();
                Matcher tdMatcher = TD_PATTERN.matcher(row);
                while (tdMatcher.find()) {
                    String cellContent = tdMatcher.group(1).trim();
                    cells.add(resolveI18nText(cellContent, props));
                }
                if (!cells.isEmpty()) {
                    pairs.add(cells.toArray(new String[0]));
                }
            }
        }
        return pairs;
    }

    private static String resolveI18nText(String text, Properties props) {
        Matcher matcher = I18N_KEY_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = props.getProperty(key, "");
            // MessageFormat uses '' for a literal apostrophe
            value = value.replace("''", "'");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        // Convert <code>...</code> to backtick-quoted text
        result = result.replaceAll("<code>([^<]*)</code>", "`$1`");
        // Strip remaining XML/HTML tags, keeping their text content
        result = result.replaceAll("<[^>]+>", "");
        // Escape pipe characters so they don't break markdown table cells
        result = result.replace("|", "\\|");
        return result.trim();
    }

    private static List<String> findSnippetReferences(String jellyContent) {
        List<String> files = new ArrayList<>();
        if (jellyContent == null) {
            return files;
        }
        Matcher matcher = CODE_FILE_PATTERN.matcher(jellyContent);
        while (matcher.find()) {
            String file = matcher.group(1);
            if (!files.contains(file)) {
                files.add(file);
            }
        }
        return files;
    }

    private static String readResource(UISample sample, String componentName, String filename, URL pluginResourceBase) {
        if (pluginResourceBase != null) {
            String base = pluginResourceBase.toExternalForm();
            if (!base.endsWith("/")) {
                base += "/";
            }
            try (InputStream is = new URL(base + componentName + "/" + filename).openStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                // fall through
            }
        }

        try (InputStream is = sample.getClass().getClassLoader().getResourceAsStream(componentName + "/" + filename)) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // fall through
        }

        String classpathPath = sample.getClass().getName().replace('.', '/') + "/" + filename;
        try (InputStream is = sample.getClass().getClassLoader().getResourceAsStream(classpathPath)) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // skip
        }

        return null;
    }

    private static String inferLanguage(String filename) {
        if (filename.endsWith(".js")) {
            return "javascript";
        }
        if (filename.endsWith(".java")) {
            return "java";
        }
        if (filename.endsWith(".properties")) {
            return "properties";
        }
        return "xml";
    }
}
