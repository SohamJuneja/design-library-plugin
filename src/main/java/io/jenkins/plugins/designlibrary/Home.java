package io.jenkins.plugins.designlibrary;

import hudson.Extension;
import hudson.PluginWrapper;
import hudson.model.RootAction;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Entry point to all the UI samples.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Home implements RootAction {

    public String getIconFileName() {
        return "symbol-design-library plugin-design-library";
    }

    public String getDisplayName() {
        return "Design Library";
    }

    public String getUrlName() {
        return "design-library";
    }

    @Override
    public boolean isPrimaryAction() {
        return true;
    }

    public List<UISample> getAll() {
        return UISample.getAll();
    }

    public static Map<Category, List<UISample>> getGrouped() {
        return UISample.getGrouped();
    }

    /**
     * Dynamically retrieves the appropriate UI sample based on the current URL
     */
    public UISample getDynamic(String name) {
        for (UISample ui : getAll()) {
            String urlName = ui.getUrlName();
            if (urlName != null && urlName.equals(name)) {
                return ui;
            }
        }
        return null;
    }

    /**
     * Serves LLM-friendly content as plain text markdown.
     *
     * <ul>
     *   <li>{@code llms.txt} - index of all components with links to individual pages</li>
     *   <li>{@code llms-all.txt} - all component documentation in a single file</li>
     *   <li>{@code {component}.md} - documentation for a single component</li>
     * </ul>
     */
    public void doDynamic(StaplerRequest2 req, StaplerResponse2 rsp) throws IOException {
        String restOfPath = req.getRestOfPath();
        if (restOfPath.startsWith("/")) {
            restOfPath = restOfPath.substring(1);
        }

        String content = resolveLlmContent(restOfPath, req);
        if (content != null) {
            rsp.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter w = rsp.getWriter()) {
                w.write(content);
            }
            return;
        }

        rsp.sendError(404);
    }

    private String resolveLlmContent(String name, StaplerRequest2 req) {
        String baseUrl = req.getContextPath() + "/" + getUrlName() + "/";

        if ("llms.txt".equals(name)) {
            return LlmContent.generateIndex(baseUrl);
        }

        URL resourceBase = getPluginResourceBase();

        if ("llms-all.txt".equals(name)) {
            return LlmContent.generateAll(resourceBase);
        }
        if (name.endsWith(".md")) {
            String componentName = name.substring(0, name.length() - 3);
            for (UISample sample : getAll()) {
                if (componentName.equals(sample.getUrlName())) {
                    return LlmContent.generateComponentMarkdown(sample, resourceBase);
                }
            }
        }
        return null;
    }

    private URL getPluginResourceBase() {
        PluginWrapper plugin = Jenkins.get().getPluginManager().getPlugin("design-library");
        return plugin != null ? plugin.baseResourceURL : null;
    }

    public String getPluginVersion() {
        Jenkins jenkins = Jenkins.get();
        PluginWrapper plugin = jenkins.getPluginManager().getPlugin("design-library");
        if (plugin != null) {
            return plugin.getVersion();
        }
        return "Version not available";
    }

    /**
     * Generates a dynamic gradient for the Home cards
     */
    public String buildGradientVar() {
        return GradientFactory.buildGradientVar();
    }

    private static final class GradientFactory {

        private static final int LAYERS = 10;

        private static final String[] COLOURS = {
            "var(--light-orange)",
            "var(--light-cyan)",
            "var(--light-pink)",
            "var(--light-red)",
            "var(--light-yellow)",
            "var(--light-purple)",
            "var(--light-teal)",
            "var(--light-indigo)",
            "var(--light-brown)"
        };

        private GradientFactory() {}

        public static String buildGradientVar() {
            StringBuilder css = new StringBuilder("--aurora").append(": \n  ");

            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            for (int i = 0; i < LAYERS; i++) {
                int x = rnd.nextInt(101);
                int y = rnd.nextInt(101);
                String colour = COLOURS[rnd.nextInt(COLOURS.length)];

                css.append(String.format("radial-gradient(at %d%% %d%%, %s 0, transparent 50%%)", x, y, colour));

                css.append(i < LAYERS - 1 ? ",\n  " : ";");
            }
            return css.toString();
        }
    }
}
