package team.jit.technicalinterviewdemo.technical;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class HelloControllerIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void rootEndpointReturnsTechnicalOverview() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.build.name").value("technical-interview-demo"))
                .andExpect(jsonPath("$.build.group").value("team.jit"))
                .andExpect(jsonPath("$.build.artifact").value("technical-interview-demo"))
                .andExpect(jsonPath("$.build.version").isString())
                .andExpect(jsonPath("$.build.time").isString())
                .andExpect(jsonPath("$.git.branch").isString())
                .andExpect(jsonPath("$.git.commitId").isString())
                .andExpect(jsonPath("$.git.shortCommitId").isString())
                .andExpect(jsonPath("$.git.commitTime").isString())
                .andExpect(jsonPath("$.runtime.applicationName").value("technical-interview-demo"))
                .andExpect(jsonPath("$.runtime.javaVersion").isString())
                .andExpect(jsonPath("$.runtime.javaVendor").isString())
                .andExpect(jsonPath("$.runtime.activeProfiles", hasItem("test")))
                .andExpect(jsonPath("$.dependencies.springBoot").value(SpringBootVersion.getVersion()))
                .andExpect(jsonPath("$.dependencies.springFramework").value(SpringVersion.getVersion()))
                .andExpect(jsonPath("$.configuration.pagination.defaultPageSize").value(20))
                .andExpect(jsonPath("$.configuration.pagination.maxPageSize").value(100))
                .andExpect(jsonPath("$.configuration.session.storeType").value("jdbc"))
                .andExpect(jsonPath("$.configuration.session.timeout").value("30m"))
                .andExpect(jsonPath("$.configuration.session.cookieName").value("technical-interview-demo-session"))
                .andExpect(jsonPath("$.configuration.session.cookieHttpOnly").value(true))
                .andExpect(jsonPath("$.configuration.session.cookieSameSite").value("lax"))
                .andExpect(jsonPath("$.configuration.observability.exposedEndpoints", hasItems("health", "info", "prometheus")))
                .andExpect(jsonPath("$.configuration.observability.healthProbesEnabled").value(true))
                .andExpect(jsonPath("$.configuration.observability.tracingSamplingProbability").value(1.0d))
                .andExpect(jsonPath("$.configuration.documentation.html").value("/docs"))
                .andExpect(jsonPath("$.configuration.documentation.openApiJson").value("/v3/api-docs"))
                .andExpect(jsonPath("$.configuration.documentation.openApiYaml").value("/v3/api-docs.yaml"))
                .andExpect(jsonPath("$.configuration.documentation.openApiVersion").value("OPENAPI_3_0"))
                .andExpect(jsonPath("$.configuration.security.csrfEnabled").value(false))
                .andExpect(jsonPath("$.configuration.security.oauthProfileActive").value(false))
                .andExpect(jsonPath("$.configuration.security.oauthLoginPath").value("/oauth2/authorization/github"))
                .andExpect(jsonPath("$.configuration.shutdown.serverShutdown").value("graceful"))
                .andExpect(jsonPath("$.configuration.shutdown.timeoutPerShutdownPhase").value("20s"));
    }

    @Test
    void helloEndpointReturnsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}
