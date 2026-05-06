package team.jit.technicalinterviewdemo.business.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@RestDocsIntegrationSpringBootTest
class AdminUserManagementApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    private BrowserSession adminSession;
    private UserAccount readerUser;

    @BeforeEach
    void setUp() throws Exception {
        auditLogRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userAccountRepository.deleteAll();

        adminSession = adminBrowserSession(sessionRepository);
        synchronizeAccount(adminSession, "admin-user");
        readerUser = synchronizeAccount(authenticatedBrowserSession(sessionRepository, "reader-user"), "reader-user");
    }

    @Test
    void documentListAdminUsersEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(adminSession.authenticatedSession())).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "admin-users/list-admin-users", responseHeaders(commonResponseHeaders()), relaxedResponseFields(
                fieldWithPath("[].id").description("Persisted user identifier."), fieldWithPath("[].provider").description("OAuth provider name."), fieldWithPath("[].login").description("External login from the OAuth provider."), fieldWithPath("[].displayName").description("Current display name from the provider."), fieldWithPath("[].email").description("Current email when available from the provider."), fieldWithPath("[].preferredLanguage").description("Preferred two-letter language code, or `null` when unset."), fieldWithPath("[].roles[]").description("Current roles assigned to the user."), fieldWithPath("[].roleGrants[].role").description("Granted role name."), fieldWithPath("[].roleGrants[].source").description("How the role was granted."), fieldWithPath("[].roleGrants[].grantedAt").description("UTC instant when the role grant was recorded."), fieldWithPath("[].roleGrants[].grantedByUserId").optional().description("Granting application user id for manually managed grants."), fieldWithPath("[].roleGrants[].grantedByLogin").optional().description("Granting application user login for manually managed grants."), fieldWithPath("[].roleGrants[].reason").optional().description("Operator-supplied reason for manually managed grants."), fieldWithPath("[].lastLoginAt").description("Latest authenticated request as a UTC instant."), fieldWithPath("[].createdAt").description("Creation timestamp as a UTC instant."), fieldWithPath("[].updatedAt").description("Last update timestamp as a UTC instant.")
            )
        ));
    }

    @Test
    void documentReplaceManagedUserRolesEndpoint() throws Exception {
        mockMvc.perform(put("/api/admin/users/{id}/roles", readerUser.getId()).with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "roles": ["USER", "ADMIN"],
              "reason": "Needs audit review access."
            }
            """)).andExpect(status().isOk()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andDo(documentEndpoint(
            "admin-users/replace-managed-user-roles", pathParameters(
                parameterWithName("id").description("Persisted user identifier.")
            ), requestBody(), requestFields(
                fieldWithPath("roles").description("Replacement managed role set. `USER` must always be present."), fieldWithPath("roles[]").description("Role name."), fieldWithPath("reason").description("Short operator-supplied explanation for the role change.")
            ), responseHeaders(commonResponseHeaders()), relaxedResponseFields(
                fieldWithPath("id").description("Persisted user identifier."), fieldWithPath("provider").description("OAuth provider name."), fieldWithPath("login").description("External login from the OAuth provider."), fieldWithPath("roles[]").description("Current roles after replacement."), fieldWithPath("roleGrants[].role").description("Granted role name."), fieldWithPath("roleGrants[].source").description("How the role was granted."), fieldWithPath("roleGrants[].grantedAt").description("UTC instant when the role grant was recorded."), fieldWithPath("roleGrants[].grantedByUserId").optional().description("Granting application user id for manually managed grants."), fieldWithPath("roleGrants[].grantedByLogin").optional().description("Granting application user login for manually managed grants."), fieldWithPath("roleGrants[].reason").optional().description("Operator-supplied reason for manually managed grants.")
            )
        ));
    }

    @Test
    void documentListAdminUsersForbiddenError() throws Exception {
        BrowserSession readerSession = authenticatedBrowserSession(sessionRepository, "reader-user");

        mockMvc.perform(get("/api/admin/users").with(readerSession.authenticatedSession())).andExpect(status().isForbidden()).andExpect(header().exists("X-Request-Id")).andExpect(header().exists("traceparent")).andExpect(jsonPath("$.title").value("Forbidden")).andDo(documentEndpoint(
            "errors/list-admin-users-forbidden", responseHeaders(commonResponseHeaders()), relaxedResponseFields(problemResponseFields())
        ));
    }

    private UserAccount synchronizeAccount(BrowserSession session, String login) throws Exception {
        mockMvc.perform(get("/api/account").with(session.authenticatedSession())).andExpect(status().isOk());
        return userAccountRepository.findByProviderAndExternalLogin("github", login).orElseThrow();
    }
}
