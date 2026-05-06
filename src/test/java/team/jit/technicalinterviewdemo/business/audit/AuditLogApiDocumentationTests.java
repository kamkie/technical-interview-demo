package team.jit.technicalinterviewdemo.business.audit;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;

@RestDocsIntegrationSpringBootTest
class AuditLogApiDocumentationTests extends AbstractDocumentationIntegrationTest {

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
    void documentListAuditLogsEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(adminOauthUser()).queryParam("targetType", "LOCALIZATION_MESSAGE").queryParam("actorLogin", "admin-user").queryParam("page", "0").queryParam("size", "20").queryParam("sort", "id,desc")).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
                "audit/list-audit-logs", queryParameters(
                        parameterWithName("targetType").optional().description(
                                "Exact audit target type filter. Supported values: `BOOK`, `CATEGORY`, `LOCALIZATION_MESSAGE`, `USER_ACCOUNT`, `AUTHENTICATION`."
                        ), parameterWithName("action").optional().description(
                                "Exact audit action filter. Supported values: `CREATE`, `UPDATE`, `DELETE`, `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `LOGOUT`, `SESSION_REJECTION`."
                        ), parameterWithName("actorLogin").optional().description("Exact actor login filter."), parameterWithName("page").optional().description("Zero-based page index."), parameterWithName("size").optional().description("Page size capped by the server."), parameterWithName("sort").optional().description(
                                "Sort expression in the form `property,direction`. Repeat the parameter for multiple sort fields. Supported properties: `id`, `targetType`, `targetId`, `action`, `actorLogin`, `createdAt`. Default sort is newest-first by `id,desc`."
                        )
                ), responseHeaders(commonResponseHeaders()), relaxedResponseFields(
                        fieldWithPath("content[].id").description("Audit log identifier."), fieldWithPath("content[].targetType").description("Type of audited target."), fieldWithPath("content[].targetId").description("Identifier of the audited target."), fieldWithPath("content[].action").description("Recorded action."), fieldWithPath("content[].actorLogin").description("Login of the acting user, or `system` for non-user writes."), fieldWithPath("content[].summary").description("Human-readable audit summary."), subsectionWithPath("content[].details").description("Compact structured audit details for ADMIN review."), fieldWithPath("content[].createdAt").description("Creation timestamp as a UTC instant."), subsectionWithPath("pageable").description("Pagination request metadata."), subsectionWithPath("sort").description("Applied sort metadata."), fieldWithPath("totalPages").description("Total number of pages."), fieldWithPath("totalElements").description("Total number of matching audit log entries."), fieldWithPath("last").description("Whether this page is the last page."), fieldWithPath("size").description("Requested page size."), fieldWithPath("number").description("Current zero-based page index."), fieldWithPath("numberOfElements").description("Number of audit entries returned in the current page."), fieldWithPath("first").description("Whether this page is the first page."), fieldWithPath("empty").description("Whether the page content is empty.")
                )
        ));
    }

    @Test
    void documentListAuditLogsUnauthorizedError() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.title").value("Unauthorized")).andDo(documentEndpoint(
                "errors/list-audit-logs-unauthorized", relaxedResponseFields(problemResponseFields())
        ));
    }

    @Test
    void documentListAuditLogsForbiddenError() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(oauthUser("reader-user"))).andExpect(status().isForbidden()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Forbidden")).andDo(documentEndpoint(
                "errors/list-audit-logs-forbidden", responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }
}
