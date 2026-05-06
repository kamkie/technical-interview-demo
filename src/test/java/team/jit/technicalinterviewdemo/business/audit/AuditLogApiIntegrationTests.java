package team.jit.technicalinterviewdemo.business.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class AuditLogApiIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog updateBookLog;
    private AuditLog deleteLocalizationLog;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        auditLogRepository.saveAndFlush(new AuditLog(
            AuditTargetType.BOOK, 101L, AuditAction.CREATE, null, "reader-user", "Created book 'Spring in Action' with ISBN 9781617297571.", Map.of(
                "title", "Spring in Action", "isbn", "9781617297571"
            )
        ));
        updateBookLog = auditLogRepository.saveAndFlush(new AuditLog(
            AuditTargetType.BOOK, 102L, AuditAction.UPDATE, null, "admin-user", "Updated book 'Clean Code' with ISBN 9780132350884.", Map.of(
                "title", "Clean Code", "isbn", "9780132350884", "publicationYear", 2008
            )
        ));
        deleteLocalizationLog = auditLogRepository.saveAndFlush(new AuditLog(
            AuditTargetType.LOCALIZATION_MESSAGE, 103L, AuditAction.DELETE, null, "admin-user", "Deleted localization message 'error.book.not_found' in language fr.", Map.of(
                "messageKey", "error.book.not_found", "language", "fr"
            )
        ));
    }

    @Test
    void listAuditLogsWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.title").value("Unauthorized")).andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"));
    }

    @Test
    void listAuditLogsAsRegularUserReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(oauthUser("reader-user"))).andExpect(status().isForbidden()).andExpect(jsonPath("$.title").value("Forbidden")).andExpect(jsonPath("$.detail").value("Audit log review requires the ADMIN role.")).andExpect(jsonPath("$.messageKey").value("error.request.forbidden")).andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void listAuditLogsReturnsNewestFirstPaginatedResponse() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(adminOauthUser()).queryParam("page", "0").queryParam("size", "2")).andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(2)).andExpect(jsonPath("$.content[0].id").value(deleteLocalizationLog.getId())).andExpect(jsonPath("$.content[0].targetType").value("LOCALIZATION_MESSAGE")).andExpect(jsonPath("$.content[0].action").value("DELETE")).andExpect(jsonPath("$.content[0].actorLogin").value("admin-user")).andExpect(jsonPath("$.content[0].details.messageKey").value("error.book.not_found")).andExpect(jsonPath("$.content[0].details.language").value("fr")).andExpect(jsonPath("$.content[0].createdAt").value(endsWith("Z"))).andExpect(jsonPath("$.content[1].id").value(updateBookLog.getId())).andExpect(jsonPath("$.content[1].details.title").value("Clean Code")).andExpect(jsonPath("$.content[1].details.publicationYear").value(2008)).andExpect(jsonPath("$.totalElements").value(3)).andExpect(jsonPath("$.totalPages").value(2)).andExpect(jsonPath("$.number").value(0)).andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void listAuditLogsCanFilterByTargetTypeActionAndActorLogin() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(adminOauthUser()).queryParam("targetType", "LOCALIZATION_MESSAGE").queryParam("action", "DELETE").queryParam("actorLogin", "admin-user")).andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1)).andExpect(jsonPath("$.content[0].id").value(deleteLocalizationLog.getId())).andExpect(jsonPath("$.content[0].targetId").value(103)).andExpect(jsonPath("$.content[0].summary").value(
            "Deleted localization message 'error.book.not_found' in language fr."
        )).andExpect(jsonPath("$.content[0].details.messageKey").value("error.book.not_found")).andExpect(jsonPath("$.content[0].details.language").value("fr")).andExpect(jsonPath("$.content[0].createdAt").value(endsWith("Z"))).andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void listAuditLogsWithUnsupportedSortReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs").with(adminOauthUser()).queryParam("sort", "dropTable,asc")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Invalid Request")).andExpect(jsonPath("$.detail").value(
            "Sort field 'dropTable' is not supported. Use one of: id, targetType, targetId, action, actorLogin, createdAt."
        ));
    }
}
