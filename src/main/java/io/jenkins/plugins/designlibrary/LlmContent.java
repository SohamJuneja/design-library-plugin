package io.jenkins.plugins.designlibrary;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
class LlmContent {

    private static final Pattern CODE_FILE_PATTERN = Pattern.compile("file=\"([^\"]+)\"");
    private static final Pattern INLINE_CODE_ELEMENT_PATTERN =
            Pattern.compile("<[a-zA-Z]+:code\\b(.*?)/>", Pattern.DOTALL);
    private static final Pattern ATTR_CODE_PATTERN = Pattern.compile("\\bcode=\"([^\"]*)\"");
    private static final Pattern ATTR_CALLBACK_PATTERN = Pattern.compile("\\bcallback=\"([^\"]*)\"");
    private static final Pattern DOS_DONTS_PATTERN =
            Pattern.compile("<s:dos-donts>(.*?)</s:dos-donts>", Pattern.DOTALL);
    private static final Pattern TR_PATTERN = Pattern.compile("<tr>(.*?)</tr>", Pattern.DOTALL);
    private static final Pattern TD_PATTERN = Pattern.compile("<td>(.*?)</td>", Pattern.DOTALL);
    private static final Pattern I18N_KEY_PATTERN = Pattern.compile("\\$\\{%([^}]+)\\}");
    private static final Pattern SECTION_PATTERN =
            Pattern.compile("<s:section\\b([^>]*)>(.*?)</s:section>", Pattern.DOTALL);
    private static final Pattern ATTR_TITLE_PATTERN = Pattern.compile("\\btitle=\"([^\"]*)\"");
    private static final Pattern ATTR_DESCRIPTION_PATTERN = Pattern.compile("\\bdescription=\"([^\"]*)\"");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile(
            "<(?:p|div)\\b[^>]*class=\"[^\"]*(?:jdl-paragraph|jdl-section__description|jdl-important-point)[^\"]*\"[^>]*>(.*?)</(?:p|div)>",
            Pattern.DOTALL);
    private static final Pattern ALERT_PATTERN =
            Pattern.compile("<div\\b[^>]*class=\"[^\"]*jenkins-alert[^\"]*\"[^>]*>(.*?)</div>", Pattern.DOTALL);
    private static final Pattern LIST_PATTERN =
            Pattern.compile("<ul\\b[^>]*class=\"[^\"]*jdl-list[^\"]*\"[^>]*>(.*?)</ul>", Pattern.DOTALL);
    private static final Pattern LI_PATTERN = Pattern.compile("<li\\b[^>]*>(.*?)</li>", Pattern.DOTALL);
    private static final Pattern PREVIEW_STRIP_PATTERN =
            Pattern.compile("<s:preview\\b[^>]*>.*?</s:preview>", Pattern.DOTALL);
    private static final Pattern TABPANE_PATTERN =
            Pattern.compile("<[a-zA-Z]+:tabPane\\b([^>]*)>(.*?)</[a-zA-Z]+:tabPane>", Pattern.DOTALL);
    private static final Pattern LINK_PATTERN =
            Pattern.compile("<a\\b([^>]*class=\"[^\"]*jdl-link[^\"]*\"[^>]*)>(.*?)</a>", Pattern.DOTALL);
    private static final Pattern ATTR_HREF_PATTERN = Pattern.compile("\\bhref=\"([^\"]*)\"");
    private static final Pattern BEM_ENTRY_PATTERN =
            Pattern.compile("<div>\\s*<dt\\b[^>]*>(.*?)</dt>((?:\\s*<dd\\b[^>]*>.*?</dd>)*)\\s*</div>", Pattern.DOTALL);
    private static final Pattern DD_PATTERN = Pattern.compile("<dd\\b[^>]*>(.*?)</dd>", Pattern.DOTALL);

    private LlmContent() {}

    private static final class ContentItem {
        static final String PARA = "PARA";
        static final String ALERT = "ALERT";
        static final String LIST = "LIST";
        static final String CODE_FILE = "CODE_FILE";
        static final String CODE_INLINE = "CODE_INLINE";
        static final String DOS_DONTS = "DOS_DONTS";
        static final String LINK = "LINK";
        static final String BEM_ENTRY = "BEM_ENTRY";

        final int start;
        final String type;
        final String content;
        // label for CODE_INLINE; href for LINK; dd blocks for BEM_ENTRY; null otherwise
        final String extra;

        ContentItem(int start, String type, String content, String extra) {
            this.start = start;
            this.type = type;
            this.content = content;
            this.extra = extra;
        }
    }

    static String generateIndex(String baseUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Jenkins Design Library\n\n");
        sb.append("> A reference library of UI components and patterns ");
        sb.append("for building Jenkins plugin interfaces.\n\n");
        sb.append("For complete documentation of all components with code examples, see [llms-all.txt](");
        sb.append(baseUrl).append("llms-all.txt).\n\n");

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

        // Colors uses j:forEach (dynamic Java data), so semantic/palette tables are generated
        // directly from the model; dos-donts are still extracted from the Jelly template.
        if (sample instanceof Colors) {
            String jellyContent = loadJellyIndex(sample);
            if (jellyContent != null) {
                Properties props = loadProperties(sample);
                Matcher sectionMatcher = SECTION_PATTERN.matcher(jellyContent);
                while (sectionMatcher.find()) {
                    Matcher titleM = ATTR_TITLE_PATTERN.matcher(sectionMatcher.group(1));
                    if (!titleM.find()) {
                        Matcher dosMatcher = DOS_DONTS_PATTERN.matcher(sectionMatcher.group(2));
                        if (dosMatcher.find()) {
                            List<String[]> pairs = extractDosDontsFromBlock(dosMatcher.group(1), props);
                            appendDosDontsMarkdown(sb, pairs);
                        }
                    }
                }
            }
            appendColorsContent(sb, (Colors) sample);
            return sb.toString();
        }

        String componentName = sample.getClass().getSimpleName();
        String jellyContent = loadJellyIndex(sample);
        if (jellyContent == null) {
            return sb.toString();
        }

        Properties props = loadProperties(sample);
        Set<String> seenFiles = new LinkedHashSet<>();

        Matcher tabPaneMatcher = TABPANE_PATTERN.matcher(jellyContent);
        if (tabPaneMatcher.find()) {
            tabPaneMatcher.reset();
            while (tabPaneMatcher.find()) {
                String tabAttrs = tabPaneMatcher.group(1);
                String tabBody = tabPaneMatcher.group(2);

                Matcher titleMatcher = ATTR_TITLE_PATTERN.matcher(tabAttrs);
                if (titleMatcher.find()) {
                    String title = resolveI18nText(titleMatcher.group(1), props);
                    if (!title.isBlank()) {
                        sb.append("## ").append(title).append("\n\n");
                    }
                }

                appendSections(sb, tabBody, props, seenFiles, sample, componentName, pluginResourceBase);
            }
        } else {
            appendSections(sb, jellyContent, props, seenFiles, sample, componentName, pluginResourceBase);
        }

        return sb.toString();
    }

    /** Scans {@code content} for {@code <s:section>} elements and appends their rendered content to sb. */
    private static void appendSections(
            StringBuilder sb,
            String content,
            Properties props,
            Set<String> seenFiles,
            UISample sample,
            String componentName,
            URL pluginResourceBase) {

        Matcher sectionMatcher = SECTION_PATTERN.matcher(content);
        while (sectionMatcher.find()) {
            String sectionAttrs = sectionMatcher.group(1);
            String sectionBody = sectionMatcher.group(2);

            Matcher titleMatcher = ATTR_TITLE_PATTERN.matcher(sectionAttrs);
            if (titleMatcher.find()) {
                String title = resolveI18nText(titleMatcher.group(1), props);
                if (!title.isBlank()) {
                    sb.append("## ").append(title).append("\n\n");
                }
            }

            Matcher descMatcher = ATTR_DESCRIPTION_PATTERN.matcher(sectionAttrs);
            if (descMatcher.find()) {
                String desc = resolveI18nText(descMatcher.group(1), props);
                if (!desc.isBlank()) {
                    sb.append(desc).append("\n\n");
                }
            }

            appendSectionContent(sb, sectionBody, props, seenFiles, sample, componentName, pluginResourceBase);
        }
    }

    private static void appendSectionContent(
            StringBuilder sb,
            String sectionBody,
            Properties props,
            Set<String> seenFiles,
            UISample sample,
            String componentName,
            URL pluginResourceBase) {

        String scanBody = PREVIEW_STRIP_PATTERN.matcher(sectionBody).replaceAll("");

        List<ContentItem> items = new ArrayList<>();

        Matcher paraMatcher = PARAGRAPH_PATTERN.matcher(scanBody);
        while (paraMatcher.find()) {
            items.add(new ContentItem(paraMatcher.start(), ContentItem.PARA, paraMatcher.group(1), null));
        }

        Matcher alertMatcher = ALERT_PATTERN.matcher(scanBody);
        while (alertMatcher.find()) {
            items.add(new ContentItem(alertMatcher.start(), ContentItem.ALERT, alertMatcher.group(1), null));
        }

        Matcher listMatcher = LIST_PATTERN.matcher(scanBody);
        while (listMatcher.find()) {
            items.add(new ContentItem(listMatcher.start(), ContentItem.LIST, listMatcher.group(1), null));
        }

        Matcher codeMatcher = INLINE_CODE_ELEMENT_PATTERN.matcher(scanBody);
        while (codeMatcher.find()) {
            String attrs = codeMatcher.group(1);
            Matcher fileMatcher = CODE_FILE_PATTERN.matcher(attrs);
            if (fileMatcher.find()) {
                items.add(new ContentItem(codeMatcher.start(), ContentItem.CODE_FILE, fileMatcher.group(1), null));
            } else {
                Matcher codeAttrMatcher = ATTR_CODE_PATTERN.matcher(attrs);
                if (codeAttrMatcher.find()) {
                    String code = codeAttrMatcher.group(1);
                    Matcher callbackMatcher = ATTR_CALLBACK_PATTERN.matcher(attrs);
                    String label = callbackMatcher.find() ? callbackMatcher.group(1) : null;
                    items.add(new ContentItem(codeMatcher.start(), ContentItem.CODE_INLINE, code, label));
                }
            }
        }

        Matcher dosDontsMatcher = DOS_DONTS_PATTERN.matcher(scanBody);
        while (dosDontsMatcher.find()) {
            items.add(new ContentItem(dosDontsMatcher.start(), ContentItem.DOS_DONTS, dosDontsMatcher.group(1), null));
        }

        Matcher linkMatcher = LINK_PATTERN.matcher(scanBody);
        while (linkMatcher.find()) {
            String linkAttrs = linkMatcher.group(1);
            String linkBody = linkMatcher.group(2);
            Matcher hrefMatcher = ATTR_HREF_PATTERN.matcher(linkAttrs);
            if (hrefMatcher.find()) {
                String href = hrefMatcher.group(1);
                if (!href.isBlank()) {
                    items.add(new ContentItem(linkMatcher.start(), ContentItem.LINK, href, linkBody));
                }
            }
        }

        // Only capture bare ${%key} tokens whose resolved value is HTML-wrapped (starts with '<'),
        // to avoid duplicating text already matched by PARAGRAPH_PATTERN.
        Matcher bareI18nMatcher = I18N_KEY_PATTERN.matcher(scanBody);
        while (bareI18nMatcher.find()) {
            int pos = bareI18nMatcher.start();
            boolean isBare =
                    pos == 0 || Character.isWhitespace(scanBody.charAt(pos - 1)) || scanBody.charAt(pos - 1) == '>';
            if (!isBare) continue;
            String key = bareI18nMatcher.group(1).trim();
            String rawValue = props.getProperty(key);
            if (rawValue == null || rawValue.isBlank()) continue;
            String trimmedValue = rawValue.trim();
            if (!trimmedValue.startsWith("<")) continue;
            // store raw HTML; resolveI18nText is called once in the render loop below
            items.add(new ContentItem(pos, ContentItem.PARA, trimmedValue, null));
        }

        Matcher bemMatcher = BEM_ENTRY_PATTERN.matcher(scanBody);
        while (bemMatcher.find()) {
            items.add(new ContentItem(
                    bemMatcher.start(), ContentItem.BEM_ENTRY, bemMatcher.group(1), bemMatcher.group(2)));
        }

        items.sort(Comparator.comparingInt(i -> i.start));

        int inlineCounter = 1;
        for (ContentItem item : items) {
            if (ContentItem.PARA.equals(item.type)) {
                String text = resolveI18nText(item.content, props);
                if (!text.isBlank()) {
                    sb.append(text).append("\n\n");
                }
            } else if (ContentItem.ALERT.equals(item.type)) {
                String text = resolveI18nText(item.content, props);
                if (!text.isBlank()) {
                    sb.append("> ").append(text).append("\n\n");
                }
            } else if (ContentItem.LIST.equals(item.type)) {
                Matcher liMatcher = LI_PATTERN.matcher(item.content);
                StringBuilder listSb = new StringBuilder();
                while (liMatcher.find()) {
                    String li = resolveI18nText(liMatcher.group(1), props);
                    if (!li.isBlank()) {
                        listSb.append("- ").append(li).append('\n');
                    }
                }
                if (!listSb.isEmpty()) {
                    sb.append(listSb).append('\n');
                }
            } else if (ContentItem.CODE_FILE.equals(item.type)) {
                String filename = item.content;
                if (seenFiles.contains(filename)) {
                    continue;
                }
                seenFiles.add(filename);
                String content = readResource(sample, componentName, filename, pluginResourceBase);
                if (content != null && !content.isBlank()) {
                    String label = filename.replaceFirst("\\.[^.]+$", "");
                    String language = inferLanguage(filename);
                    sb.append("### ").append(label).append("\n\n");
                    sb.append("```").append(language).append('\n');
                    sb.append(content.strip()).append('\n');
                    sb.append("```\n\n");
                }
            } else if (ContentItem.CODE_INLINE.equals(item.type)) {
                String label = item.extra != null ? item.extra : "snippet" + inlineCounter++;
                sb.append("### ").append(label).append("\n\n");
                sb.append("```javascript\n");
                sb.append(item.content).append('\n');
                sb.append("```\n\n");
            } else if (ContentItem.DOS_DONTS.equals(item.type)) {
                List<String[]> pairs = extractDosDontsFromBlock(item.content, props);
                appendDosDontsMarkdown(sb, pairs);
            } else if (ContentItem.LINK.equals(item.type)) {
                String linkText = resolveI18nText(item.extra, props);
                if (!linkText.isBlank()) {
                    sb.append("[")
                            .append(linkText)
                            .append("](")
                            .append(item.content)
                            .append(")\n\n");
                }
            } else if (ContentItem.BEM_ENTRY.equals(item.type)) {
                String dtText = resolveI18nText(item.content, props);
                if (!dtText.isBlank()) {
                    StringBuilder bemSb = new StringBuilder();
                    bemSb.append("**").append(dtText).append("**");
                    if (item.extra != null && !item.extra.isBlank()) {
                        Matcher ddMatcher = DD_PATTERN.matcher(item.extra);
                        String sep = ": ";
                        while (ddMatcher.find()) {
                            String ddText = resolveI18nText(ddMatcher.group(1), props);
                            if (!ddText.isBlank()) {
                                bemSb.append(sep).append(ddText);
                                sep = ". ";
                            }
                        }
                    }
                    sb.append(bemSb).append("\n\n");
                }
            }
        }
    }

    private static void appendColorsContent(StringBuilder sb, Colors colors) {
        sb.append("## Semantic\n\n");
        sb.append("| Name | Description | CSS Class | CSS Variable |\n");
        sb.append("|---|---|---|---|\n");
        for (Colors.Semantic s : colors.getSemantics()) {
            sb.append("| ")
                    .append(s.getName())
                    .append(" | ")
                    .append(s.getDescription())
                    .append(" | `jenkins-!-")
                    .append(s.getVariable())
                    .append("`")
                    .append(" | `var(--")
                    .append(s.getVariable())
                    .append(")`")
                    .append(" |\n");
        }
        sb.append('\n');

        sb.append("## Palette\n\n");
        sb.append("| Name | CSS Class | CSS Variable |\n");
        sb.append("|---|---|---|\n");
        for (Colors.Color c : colors.getColors()) {
            sb.append("| ")
                    .append(c.getName())
                    .append(" | `jenkins-!-")
                    .append(c.getVariable())
                    .append("`")
                    .append(" | `var(--")
                    .append(c.getClassName())
                    .append(")`")
                    .append(" |\n");
        }
        sb.append('\n');
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

    private static List<String[]> extractDosDontsFromBlock(String block, Properties props) {
        List<String[]> pairs = new ArrayList<>();
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
        return pairs;
    }

    private static void appendDosDontsMarkdown(StringBuilder sb, List<String[]> pairs) {
        List<String> dos = new ArrayList<>();
        List<String> donts = new ArrayList<>();
        for (String[] pair : pairs) {
            if (pair.length > 0 && !pair[0].isBlank()) {
                dos.add(pair[0]);
            }
            if (pair.length > 1 && !pair[1].isBlank()) {
                donts.add(pair[1]);
            }
        }

        if (dos.isEmpty() && donts.isEmpty()) {
            return;
        }

        sb.append("### Dos and Don'ts\n\n");
        appendDosDontsList(sb, "Do", dos);
        appendDosDontsList(sb, "Don't", donts);
    }

    private static void appendDosDontsList(StringBuilder sb, String label, List<String> items) {
        if (items.isEmpty()) {
            return;
        }

        sb.append("**").append(label).append("**\n");
        for (String item : items) {
            sb.append("- ").append(item).append('\n');
        }
        sb.append('\n');
    }

    private static String resolveI18nText(String text, Properties props) {
        Matcher matcher = I18N_KEY_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = props.getProperty(key, key);
            // MessageFormat uses '' for a literal apostrophe
            value = value.replace("''", "'");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        String result = sb.toString();

        // Convert <code> spans to backtick placeholders before tag stripping,
        // so that Java generics like <T> inside code spans survive intact.
        List<String> backtickSpans = new ArrayList<>();
        Matcher codeMatcher = Pattern.compile("<code\\b[^>]*>([^<]*)</code>").matcher(result);
        StringBuffer codeBuf = new StringBuffer();
        while (codeMatcher.find()) {
            String inner = codeMatcher
                    .group(1)
                    .strip()
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&amp;", "&")
                    .replace("&quot;", "\"")
                    .replace("&apos;", "'");
            String ph = "\u0002" + backtickSpans.size() + "\u0003";
            backtickSpans.add("`" + inner + "`");
            codeMatcher.appendReplacement(codeBuf, Matcher.quoteReplacement(ph));
        }
        codeMatcher.appendTail(codeBuf);
        result = codeBuf.toString();

        // Protect &lt;tagname> (half-encoded tag names used in display text) before entity
        // decoding, otherwise &lt;a> becomes <a> and the tag stripper removes it.
        result = result.replaceAll("&lt;([a-zA-Z][^>\"'<]*?)>", "\u0004$1\u0005");
        result = decodeNumericEntities(result);
        result = result.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
        // Replace <br/> with a space so adjacent sentences don't merge into one
        result = result.replaceAll("<br\\s*/?>", " ");
        result = result.replaceAll("<[^>]+>", "");
        result = result.replaceAll("\\s+", " ");
        // Restore backtick spans after tag stripping so <T> generics inside them are preserved
        for (int i = 0; i < backtickSpans.size(); i++) {
            result = result.replace("\u0002" + i + "\u0003", backtickSpans.get(i));
        }
        result = result.replaceAll("\u0004([^\u0005]*)\u0005", "<$1>");
        // Escape pipe characters so they don't break markdown table cells
        result = result.replace("|", "\\|");
        return result.trim();
    }

    private static String decodeNumericEntities(String text) {
        Matcher m = Pattern.compile("&#(x?)([0-9a-fA-F]+);").matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int codePoint = m.group(1).isEmpty() ? Integer.parseInt(m.group(2)) : Integer.parseInt(m.group(2), 16);
            m.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf((char) codePoint)));
        }
        m.appendTail(sb);
        return sb.toString();
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
