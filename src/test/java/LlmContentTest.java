import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import org.htmlunit.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@TestInstance(PER_CLASS)
class LlmContentTest {

    private JenkinsRule jenkins;

    @BeforeAll
    void beforeAll(JenkinsRule jenkins) {
        this.jenkins = jenkins;
    }

    @Test
    void llmsTxtContainsIndex() throws Exception {
        try (var webClient = jenkins.createWebClient()) {
            Page page = webClient.getPage(jenkins.getURL() + "design-library/llms.txt");
            String content = page.getWebResponse().getContentAsString();

            assertThat(content).startsWith("# Jenkins Design Library");
            assertThat(content).contains("## Components");
            assertThat(content).contains("## Patterns");
            assertThat(content).contains("Buttons");
        }
    }

    @Test
    void llmsAllTxtContainsAllComponents() throws Exception {
        try (var webClient = jenkins.createWebClient()) {
            Page page = webClient.getPage(jenkins.getURL() + "design-library/llms-all.txt");
            String content = page.getWebResponse().getContentAsString();

            assertThat(content).startsWith("# Jenkins Design Library");
            assertThat(content).contains("# Buttons");
            assertThat(content).contains("# Cards");
            assertThat(content).contains("# Colors");
        }
    }

    @Test
    void existingComponentPagesStillWork() throws Exception {
        try (var webClient = jenkins.createWebClient().withJavaScriptEnabled(false)) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setPrintContentOnFailingStatusCode(false);
            Page page = webClient.getPage(jenkins.getURL() + "design-library/buttons");
            assertThat(page.getWebResponse().getStatusCode()).isEqualTo(200);
        }
    }
}
