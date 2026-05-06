package team.jit.technicalinterviewdemo.technical.operator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;

import java.util.Map;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

@RestDocsIntegrationSpringBootTest
class OperatorSurfaceApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        auditLogRepository.saveAndFlush(new AuditLog(
            AuditTargetType.BOOK, 101L, AuditAction.CREATE, null, "reader-user", "Created book 'Spring in Action' with ISBN 9781617297571.", Map.of(
                "title", "Spring in Action", "isbn", "9781617297571"
            )
        ));
        auditLogRepository.saveAndFlush(new AuditLog(
            AuditTargetType.LOCALIZATION_MESSAGE, 103L, AuditAction.DELETE, null, "admin-user", "Deleted localization message 'error.book.not_found' in language fr.", Map.of(
                "messageKey", "error.book.not_found", "language", "fr"
            )
        ));
    }

    @Test
    void documentGetOperatorSurfaceEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface").with(adminOauthUser())).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "operator/get-operator-surface", responseHeaders(commonResponseHeaders()), relaxedResponseFields(
                fieldWithPath("audit.auditLogEndpoint").description("Endpoint path for full audit log pagination."), fieldWithPath("audit.totalEntries").description("Current number of stored audit rows."), fieldWithPath("audit.recentEntries[].id").description("Audit log identifier."), fieldWithPath("audit.recentEntries[].targetType").description("Type of audited target."), fieldWithPath("audit.recentEntries[].targetId").description("Identifier of the audited target."), fieldWithPath("audit.recentEntries[].action").description("Recorded action."), fieldWithPath("audit.recentEntries[].actorLogin").description("Login of the acting user."), fieldWithPath("audit.recentEntries[].summary").description("Human-readable audit summary."), subsectionWithPath("audit.recentEntries[].details").description("Compact structured audit details for each recent audit entry."), fieldWithPath("audit.recentEntries[].createdAt").description("Creation timestamp as a UTC instant."), fieldWithPath("runtime.technicalOverviewEndpoint").description("Endpoint path for the full technical overview payload."), subsectionWithPath("runtime.technicalOverview").description("Current technical overview payload from `GET /`."), fieldWithPath("operations.actuatorHealthEndpoint").description("Endpoint path for aggregate health."), fieldWithPath("operations.actuatorInfoEndpoint").description("Endpoint path for build and git metadata."), fieldWithPath("operations.actuatorPrometheusEndpoint").description("Endpoint path for trusted deployment metrics scraping."), fieldWithPath("operations.applicationHealthStatus").description("Current aggregate health status code."), fieldWithPath("operations.livenessState").description("Current Spring Boot liveness state."), fieldWithPath("operations.readinessState").description("Current Spring Boot readiness state.")
            )
        ));
    }

    @Test
    void documentGetOperatorSurfaceUnauthorizedError() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.title").value("Unauthorized")).andDo(documentEndpoint(
            "errors/get-operator-surface-unauthorized", relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentGetOperatorSurfaceForbiddenError() throws Exception {
        mockMvc.perform(get("/api/admin/operator-surface").with(oauthUser("reader-user"))).andExpect(status().isForbidden()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Forbidden")).andDo(documentEndpoint(
            "errors/get-operator-surface-forbidden", responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }
}
