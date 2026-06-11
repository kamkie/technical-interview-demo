package team.jit.technicalinterviewdemo.business.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@MockMvcIntegrationSpringBootTest
class AdminUserManagementApiIntegrationTests extends AbstractMockMvcIntegrationTest {

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

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void listAdminUsersWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.messageKey").value("error.request.unauthorized"));
    }

    @Test
    void listAdminUsersAsRegularUserReturnsForbidden() throws Exception {
        BrowserSession readerSession = readerSession();
        synchronizeAccount(readerSession, "reader-user");

        mockMvc.perform(get("/api/admin/users").with(readerSession.authenticatedSession()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("User management requires the ADMIN role."))
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"));
    }

    @Test
    void listAdminUsersReturnsPersistedUsersWithRoleGrantProvenance() throws Exception {
        BrowserSession adminSession = adminSession();
        BrowserSession readerSession = readerSession();

        synchronizeAccount(adminSession, "admin-user");
        synchronizeAccount(readerSession, "reader-user");

        mockMvc.perform(get("/api/admin/users").with(adminSession.authenticatedSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("admin-user"))
                .andExpect(jsonPath("$[0].roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$[0].roles[1]").value("USER"))
                .andExpect(jsonPath("$[0].roleGrants[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[0].roleGrants[0].source").value("BOOTSTRAP"))
                .andExpect(jsonPath("$[0].roleGrants[0].grantedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[0].roleGrants[0].grantedByUserId").doesNotExist())
                .andExpect(jsonPath("$[0].roleGrants[1].role").value("USER"))
                .andExpect(jsonPath("$[0].roleGrants[1].source").value("AUTHENTICATED_LOGIN"))
                .andExpect(jsonPath("$[0].roleGrants[1].grantedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[0].lastLoginAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[0].createdAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[0].updatedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[1].login").value("reader-user"))
                .andExpect(jsonPath("$[1].roles[0]").value("USER"))
                .andExpect(jsonPath("$[1].roleGrants[0].role").value("USER"))
                .andExpect(jsonPath("$[1].roleGrants[0].source").value("AUTHENTICATED_LOGIN"))
                .andExpect(jsonPath("$[1].roleGrants[0].grantedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[1].lastLoginAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[1].createdAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[1].updatedAt").value(endsWith("Z")));
    }

    @Test
    void replaceManagedUserRolesUpdatesProvenanceAndGrantor() throws Exception {
        BrowserSession adminSession = adminSession();
        BrowserSession readerSession = readerSession();

        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession, "reader-user");

        mockMvc.perform(put("/api/admin/users/{id}/roles", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "roles": ["USER", "ADMIN"],
                              "reason": "Needs audit review access."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("reader-user"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("USER"))
                .andExpect(jsonPath("$.roleGrants[0].source").value("ADMIN_MANAGED"))
                .andExpect(jsonPath("$.roleGrants[0].grantedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$.roleGrants[0].grantedByUserId").value(adminUser.getId()))
                .andExpect(jsonPath("$.roleGrants[0].grantedByLogin").value("admin-user"))
                .andExpect(jsonPath("$.roleGrants[0].reason").value("Needs audit review access."))
                .andExpect(jsonPath("$.lastLoginAt").value(endsWith("Z")))
                .andExpect(jsonPath("$.createdAt").value(endsWith("Z")))
                .andExpect(jsonPath("$.updatedAt").value(endsWith("Z")));

        UserAccount updatedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(updatedReader.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(updatedReader.getRoleGrants()).allSatisfy(roleGrant -> {
            assertThat(roleGrant.getGrantSource()).isEqualTo(UserRoleGrantSource.ADMIN_MANAGED);
            assertThat(roleGrant.getGrantedByUser()).isNotNull();
            assertThat(roleGrant.getGrantedByUser().getId()).isEqualTo(adminUser.getId());
            assertThat(roleGrant.getReason()).isEqualTo("Needs audit review access.");
        });

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.USER_ACCOUNT);
        assertThat(auditLog.getTargetId()).isEqualTo(readerUser.getId());
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.UPDATE);
        assertThat(auditLog.getActorLogin()).isEqualTo("admin-user");
        assertThat(auditLog.getDetails()).containsEntry("targetProvider", "github");
        assertThat(auditLog.getDetails()).containsEntry("targetLogin", "reader-user");
        assertThat(auditLog.getDetails()).containsEntry("reason", "Needs audit review access.");
        assertThat(auditLog.getDetails().get("previousRoles")).isEqualTo(java.util.List.of("USER"));
        assertThat(auditLog.getDetails().get("roles")).isEqualTo(java.util.List.of("ADMIN", "USER"));
    }

    @Test
    void replaceManagedUserRolesForBootstrapAdminDoesNotDuplicateBootstrapGrant() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");

        mockMvc.perform(put("/api/admin/users/{id}/roles", adminUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "roles": ["USER", "ADMIN"],
                              "reason": "Keep bootstrap access while replacing managed roles."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin-user"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("USER"))
                .andExpect(jsonPath("$.roleGrants[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.roleGrants[0].source").value("BOOTSTRAP"))
                .andExpect(jsonPath("$.roleGrants[0].grantedByUserId").doesNotExist())
                .andExpect(jsonPath("$.roleGrants[1].role").value("USER"))
                .andExpect(jsonPath("$.roleGrants[1].source").value("ADMIN_MANAGED"))
                .andExpect(jsonPath("$.roleGrants[1].grantedByUserId").value(adminUser.getId()))
                .andExpect(jsonPath("$.roleGrants[1].reason")
                        .value("Keep bootstrap access while replacing managed roles."));

        UserAccount updatedAdmin =
                userAccountRepository.findById(adminUser.getId()).orElseThrow();
        assertThat(updatedAdmin.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(updatedAdmin.getRoleGrants()).hasSize(2);
        assertThat(updatedAdmin.getRoleGrants())
                .filteredOn(roleGrant -> roleGrant.getRole() == UserRole.ADMIN)
                .singleElement()
                .extracting(UserRoleGrant::getGrantSource)
                .isEqualTo(UserRoleGrantSource.BOOTSTRAP);
    }

    @Test
    void replaceManagedUserRolesWithoutUserRoleReturnsBadRequest() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession(), "reader-user");

        mockMvc.perform(put("/api/admin/users/{id}/roles", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "roles": ["ADMIN"],
                              "reason": "Invalid role set"
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("USER role is required"));

        UserAccount unchangedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(unchangedReader.getRoles()).containsExactly(UserRole.USER);
        assertThat(adminUser.getRoles()).contains(UserRole.ADMIN);
    }

    @Test
    void replaceManagedUserRolesForMissingUserReturnsNotFound() throws Exception {
        BrowserSession adminSession = adminSession();
        synchronizeAccount(adminSession, "admin-user");

        mockMvc.perform(put("/api/admin/users/{id}/roles", 9999L)
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "roles": ["USER", "ADMIN"],
                              "reason": "Needs access."
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User Account Not Found"))
                .andExpect(jsonPath("$.messageKey").value("error.user.not_found"));
    }

    @Test
    void blockUserPersistsProvenanceAndWritesAuditEntry() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession(), "reader-user");

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "Abusive API usage pending review."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("reader-user"))
                .andExpect(jsonPath("$.accountStatus").value("BLOCKED"))
                .andExpect(jsonPath("$.blockedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$.blockedBy").value("admin-user"))
                .andExpect(jsonPath("$.blockedReason").value("Abusive API usage pending review."))
                .andExpect(jsonPath("$.roles[0]").value("USER"));

        UserAccount blockedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(blockedReader.isBlocked()).isTrue();
        assertThat(blockedReader.getBlockedAt()).isNotNull();
        assertThat(blockedReader.getBlockedReason()).isEqualTo("Abusive API usage pending review.");
        assertThat(blockedReader.getBlockedByUser().getId()).isEqualTo(adminUser.getId());
        assertThat(blockedReader.getRoles()).containsExactly(UserRole.USER);

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.USER_ACCOUNT);
        assertThat(auditLog.getTargetId()).isEqualTo(readerUser.getId());
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.UPDATE);
        assertThat(auditLog.getActorLogin()).isEqualTo("admin-user");
        assertThat(auditLog.getDetails())
                .containsEntry("targetProvider", "github")
                .containsEntry("targetLogin", "reader-user")
                .containsEntry("previousStatus", "ACTIVE")
                .containsEntry("status", "BLOCKED")
                .containsEntry("reason", "Abusive API usage pending review.");
    }

    @Test
    void unblockUserClearsProvenanceAndKeepsPriorRoles() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession(), "reader-user");
        readerUser.replaceManagedRoleGrants(
                java.util.Set.of(UserRole.USER, UserRole.ADMIN), adminUser, "Needs audit review access.");
        readerUser.block(adminUser, "Abusive API usage pending review.");
        userAccountRepository.saveAndFlush(readerUser);
        auditLogRepository.deleteAll();

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "ACTIVE",
                              "reason": "Review finished; restoring access."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("reader-user"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.blockedAt").doesNotExist())
                .andExpect(jsonPath("$.blockedBy").doesNotExist())
                .andExpect(jsonPath("$.blockedReason").doesNotExist())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("USER"));

        UserAccount unblockedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(unblockedReader.isBlocked()).isFalse();
        assertThat(unblockedReader.getBlockedAt()).isNull();
        assertThat(unblockedReader.getBlockedReason()).isNull();
        assertThat(unblockedReader.getBlockedByUser()).isNull();
        assertThat(unblockedReader.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.USER_ACCOUNT);
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.UPDATE);
        assertThat(auditLog.getDetails())
                .containsEntry("previousStatus", "BLOCKED")
                .containsEntry("status", "ACTIVE")
                .containsEntry("reason", "Review finished; restoring access.");
    }

    @Test
    void replacingStatusWithCurrentStatusIsIdempotentAndWritesNoAudit() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession(), "reader-user");

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "ACTIVE",
                              "reason": "Already active."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.blockedAt").doesNotExist());

        readerUser.block(adminUser, "Original block reason.");
        userAccountRepository.saveAndFlush(readerUser);

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "Different reason that must not overwrite."
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("BLOCKED"))
                .andExpect(jsonPath("$.blockedReason").value("Original block reason."));

        UserAccount unchangedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(unchangedReader.getBlockedReason()).isEqualTo("Original block reason.");
        assertThat(auditLogRepository.findAll()).isEmpty();
    }

    @Test
    void selfTargetedStatusChangeReturnsBadRequestForBothDirections() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");

        mockMvc.perform(put("/api/admin/users/{id}/status", adminUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "Trying to block myself."
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"))
                .andExpect(jsonPath("$.detail").value("Changing the status of your own account is not allowed."));

        mockMvc.perform(put("/api/admin/users/{id}/status", adminUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "ACTIVE",
                              "reason": "Trying to confirm my own status."
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request"));

        UserAccount unchangedAdmin =
                userAccountRepository.findById(adminUser.getId()).orElseThrow();
        assertThat(unchangedAdmin.isBlocked()).isFalse();
        assertThat(auditLogRepository.findAll()).isEmpty();
    }

    @Test
    void replaceStatusValidationAndAccessErrorsMatchRolesEndpointFamily() throws Exception {
        BrowserSession adminSession = adminSession();
        synchronizeAccount(adminSession, "admin-user");
        BrowserSession readerSession = readerSession();
        UserAccount readerUser = synchronizeAccount(readerSession, "reader-user");

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED"
                            }
                            """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "   "
                            }
                            """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "%s"
                            }
                            """
                                .formatted("x".repeat(256))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "reason": "Missing status."
                            }
                            """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "SUSPENDED",
                              "reason": "Unknown status value."
                            }
                            """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "No session."
                            }
                            """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/admin/users/{id}/status", readerUser.getId())
                        .with(readerSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "Not an admin."
                            }
                            """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("User management requires the ADMIN role."));

        mockMvc.perform(put("/api/admin/users/{id}/status", 9999L)
                        .with(adminSession.unsafeWrite())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "status": "BLOCKED",
                              "reason": "Unknown user."
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User Account Not Found"));

        UserAccount unchangedReader =
                userAccountRepository.findById(readerUser.getId()).orElseThrow();
        assertThat(unchangedReader.isBlocked()).isFalse();
        assertThat(auditLogRepository.findAll()).isEmpty();
    }

    @Test
    void listAdminUsersIncludesAccountStatusAndBlockProvenance() throws Exception {
        BrowserSession adminSession = adminSession();
        UserAccount adminUser = synchronizeAccount(adminSession, "admin-user");
        UserAccount readerUser = synchronizeAccount(readerSession(), "reader-user");
        readerUser.block(adminUser, "Abusive API usage pending review.");
        userAccountRepository.saveAndFlush(readerUser);

        mockMvc.perform(get("/api/admin/users").with(adminSession.authenticatedSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("admin-user"))
                .andExpect(jsonPath("$[0].accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[0].blockedAt").doesNotExist())
                .andExpect(jsonPath("$[0].blockedBy").doesNotExist())
                .andExpect(jsonPath("$[0].blockedReason").doesNotExist())
                .andExpect(jsonPath("$[1].login").value("reader-user"))
                .andExpect(jsonPath("$[1].accountStatus").value("BLOCKED"))
                .andExpect(jsonPath("$[1].blockedAt").value(endsWith("Z")))
                .andExpect(jsonPath("$[1].blockedBy").value("admin-user"))
                .andExpect(jsonPath("$[1].blockedReason").value("Abusive API usage pending review."));
    }

    private UserAccount synchronizeAccount(BrowserSession session, String login) throws Exception {
        mockMvc.perform(get("/api/account").with(session.authenticatedSession()))
                .andExpect(status().isOk());

        return userAccountRepository
                .findByProviderAndExternalLogin("github", login)
                .orElseThrow();
    }

    private BrowserSession adminSession() {
        return adminBrowserSession(sessionRepository);
    }

    private BrowserSession readerSession() {
        return authenticatedBrowserSession(sessionRepository, "reader-user");
    }
}
