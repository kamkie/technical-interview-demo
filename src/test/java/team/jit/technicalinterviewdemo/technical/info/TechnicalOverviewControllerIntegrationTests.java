package team.jit.technicalinterviewdemo.technical.info;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
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
class TechnicalOverviewControllerIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void rootEndpointReturnsTechnicalOverviewWithFrozenRuntimePosture() throws Exception {
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
                .andExpect(
                        jsonPath("$.configuration.pagination.defaultPageSize").value(20))
                .andExpect(jsonPath("$.configuration.pagination.maxPageSize").value(100))
                .andExpect(jsonPath("$.configuration.session.storeType").value("jdbc"))
                .andExpect(jsonPath("$.configuration.session.timeout").value("30m"))
                .andExpect(jsonPath("$.configuration.session.cookieName").value("technical-interview-demo-session"))
                .andExpect(jsonPath("$.configuration.session.cookieHttpOnly").value(true))
                .andExpect(jsonPath("$.configuration.session.cookieSameSite").value("lax"))
                .andExpect(jsonPath(
                        "$.configuration.observability.exposedEndpoints", contains("health", "info", "prometheus")))
                .andExpect(jsonPath("$.configuration.observability.healthProbesEnabled")
                        .value(true))
                .andExpect(jsonPath("$.configuration.observability.tracingSamplingProbability")
                        .value(1.0d))
                .andExpect(jsonPath("$.configuration.documentation.html").value("/docs"))
                .andExpect(jsonPath("$.configuration.documentation.openApiJson").value("/v3/api-docs"))
                .andExpect(jsonPath("$.configuration.documentation.openApiYaml").value("/v3/api-docs.yaml"))
                .andExpect(
                        jsonPath("$.configuration.documentation.openApiVersion").value("OPENAPI_3_0"))
                .andExpect(jsonPath("$.configuration.security.csrfEnabled").value(true))
                .andExpect(jsonPath("$.configuration.security.csrfCookieName").value("XSRF-TOKEN"))
                .andExpect(jsonPath("$.configuration.security.csrfHeaderName").value("X-XSRF-TOKEN"))
                .andExpect(
                        jsonPath("$.configuration.security.oauthProfileActive").value(false))
                .andExpect(jsonPath("$.configuration.security.publicApiPathPattern")
                        .value("/api/**"))
                .andExpect(jsonPath("$.configuration.security.oauthAuthorizationBasePath")
                        .value("/api/session/oauth2/authorization"))
                .andExpect(jsonPath("$.configuration.security.oauthCallbackPathTemplate")
                        .value("/api/session/login/oauth2/code/{registrationId}"))
                .andExpect(jsonPath("$.configuration.security.forwardHeadersStrategy")
                        .value("none"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.owner")
                        .value("edge-or-gateway"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.loginBootstrapPathTemplate")
                        .value("/api/session/oauth2/authorization/{registrationId}"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.loginBootstrapControls[0]")
                        .value("burst-rate-limiting"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.loginBootstrapControls[1]")
                        .value("challenge-or-block-suspicious-clients"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWritePathPattern")
                        .value("/api/**"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWriteExamples[0]")
                        .value("/api/session/logout"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWriteExamples[4]")
                        .value("/api/account/language"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWriteControls[0]")
                        .value("per-client-throttling"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWriteControls[1]")
                        .value("request-size-enforcement"))
                .andExpect(jsonPath("$.configuration.security.abuseProtection.unsafeWriteControls[2]")
                        .value("rejection-visibility"))
                .andExpect(jsonPath("$.configuration.shutdown.serverShutdown").value("graceful"))
                .andExpect(jsonPath("$.configuration.shutdown.timeoutPerShutdownPhase")
                        .value("20s"));
    }

    @Test
    void helloEndpointRemainsAvailableForTrustedSmokeChecks() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}
