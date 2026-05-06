package team.jit.technicalinterviewdemo.technical.operator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import java.util.Map;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

@MockMvcIntegrationSpringBootTest
class OperatorSurfaceApiIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog newestAuditLog;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        auditLogRepository.saveAndFlush(new AuditLog(
                AuditTargetType.BOOK,
                101L,
                AuditAction.CREATE,
                null,
                "reader-user",
                "Created book 'Spring in Action' with ISBN 9781617297571.",
                Map.of("title", "Spring in Action", "isbn", "9781617297571")));
        auditLogRepository.saveAndFlush(new AuditLog(
                AuditTargetType.BOOK,
                102L,
                AuditAction.UPDATE,
                null,
                "admin-user",
                "Updated book 'Clean Code' with ISBN 9780132350884.",
                Map.of("title", "Clean Code", "isbn", "9780132350884")));
        newestAuditLog = auditLogRepository.saveAndFlush(new AuditLog(
                AuditTargetType.LOCALIZATION_MESSAGE,
                103L,
                AuditAction.DELETE,
                null,
                "admin-user",
                "Deleted localization message 'error.book.not_found' in language fr.",
                Map.of("messageKey", "error.book.not_found", "language", "fr")));
    }

    @Test
    void getOperatorSurfaceWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"));
    }

    @Test
    void getOperatorSurfaceAsRegularUserReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface").with(oauthUser("reader-user")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("Operator surface requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"));
    }

    @Test
    void getOperatorSurfaceAsAdminReturnsAuditRuntimeAndOperationalSections() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface").with(adminOauthUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.audit.auditLogEndpoint").value("/api/admin/audit-logs"))
                .andExpect(jsonPath("$.audit.totalEntries").value(3))
                .andExpect(jsonPath("$.audit.recentEntries.length()").value(3))
                .andExpect(jsonPath("$.audit.recentEntries[0].id").value(newestAuditLog.getId()))
                .andExpect(
                        jsonPath("$.audit.recentEntries[0].details.messageKey").value("error.book.not_found"))
                .andExpect(jsonPath("$.audit.recentEntries[0].details.language").value("fr"))
                .andExpect(jsonPath("$.audit.recentEntries[0].createdAt").value(endsWith("Z")))
                .andExpect(jsonPath("$.runtime.technicalOverviewEndpoint").value("/"))
                .andExpect(jsonPath("$.runtime.technicalOverview.runtime.applicationName")
                        .value("technical-interview-demo"))
                .andExpect(jsonPath("$.runtime.technicalOverview.configuration.security.csrfEnabled")
                        .value(true))
                .andExpect(jsonPath("$.runtime.technicalOverview.configuration.security.csrfCookieName")
                        .value("XSRF-TOKEN"))
                .andExpect(jsonPath("$.runtime.technicalOverview.configuration.security.csrfHeaderName")
                        .value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.runtime.technicalOverview.configuration.security.abuseProtection.owner")
                        .value("edge-or-gateway"))
                .andExpect(jsonPath("$.operations.actuatorHealthEndpoint").value("/actuator/health"))
                .andExpect(jsonPath("$.operations.actuatorInfoEndpoint").value("/actuator/info"))
                .andExpect(jsonPath("$.operations.actuatorPrometheusEndpoint").value("/actuator/prometheus"))
                .andExpect(jsonPath("$.operations.applicationHealthStatus").value("UP"))
                .andExpect(jsonPath("$.operations.livenessState").exists())
                .andExpect(jsonPath("$.operations.readinessState").exists());
    }
}
