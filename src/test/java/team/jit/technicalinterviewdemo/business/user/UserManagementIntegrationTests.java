package team.jit.technicalinterviewdemo.business.user;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.CacheTestSupport;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@MockMvcIntegrationSpringBootTest
class UserManagementIntegrationTests extends AbstractMockMvcIntegrationTest {

    private static final String USER_OPERATIONS = "technical.interview.demo.users.operations";
    private static final String USER_TOTAL = "technical.interview.demo.users.total";
    private static final String ADMIN_TOTAL = "technical.interview.demo.users.admin.total";

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        userAccountRepository.deleteAll();
        clearCaches();
    }

    @Test
    void authenticatedBookWritePersistsUserWithDefaultUserRole() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(post("/api/books").with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "title": "Spring in Action",
                  "author": "Craig Walls",
                  "isbn": "9781617297571",
                  "publicationYear": 2022
                }
                """)).andExpect(status().isCreated());

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user").orElseThrow();

        assertThat(userAccount.getRoles()).containsExactly(UserRole.USER);
        assertThat(userAccount.getDisplayName()).isEqualTo("reader-user display");
        assertThat(userAccount.getEmail()).isEqualTo("reader-user@example.test");
        assertThat(userAccount.getLastLoginAt()).isNotNull();
    }

    @Test
    void bootstrapAdminIdentityGetsPersistedAdminGrantAndCanManageCategories() throws Exception {
        BrowserSession adminSession = adminSession();

        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "name": "Architecture"
                }
                """)).andExpect(status().isCreated());

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin("github", "admin-user").orElseThrow();

        assertThat(userAccount.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
        assertThat(userAccount.getRoleGrants()).extracting(UserRoleGrant::getRole, UserRoleGrant::getGrantSource).containsExactlyInAnyOrder(
                tuple(UserRole.ADMIN, UserRoleGrantSource.BOOTSTRAP), tuple(UserRole.USER, UserRoleGrantSource.AUTHENTICATED_LOGIN)
        );
    }

    @Test
    void secondBootstrapIdentityDoesNotReceiveAdminRoleAfterFirstAdminExists() throws Exception {
        BrowserSession adminSession = adminSession();
        BrowserSession secondAdminSession = authenticatedBrowserSession(sessionRepository, "second-admin");

        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "name": "Architecture"
                }
                """)).andExpect(status().isCreated());

        mockMvc.perform(post("/api/categories").with(secondAdminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "name": "Operations"
                }
                """)).andExpect(status().isForbidden()).andExpect(jsonPath("$.detail").value("Category management requires the ADMIN role."));

        UserAccount secondAdmin = userAccountRepository.findByProviderAndExternalLogin("github", "second-admin").orElseThrow();

        assertThat(secondAdmin.getRoles()).containsExactly(UserRole.USER);
        assertThat(secondAdmin.getRoleGrants()).extracting(UserRoleGrant::getRole, UserRoleGrant::getGrantSource).containsExactly(tuple(UserRole.USER, UserRoleGrantSource.AUTHENTICATED_LOGIN));
    }

    @Test
    void currentUserEndpointReturnsPersistedProfile() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession())).andExpect(status().isOk()).andExpect(jsonPath("$.provider").value("github")).andExpect(jsonPath("$.login").value("reader-user")).andExpect(jsonPath("$.displayName").value("reader-user display")).andExpect(jsonPath("$.email").value("reader-user@example.test")).andExpect(jsonPath("$.roles[0]").value("USER")).andExpect(jsonPath("$.lastLoginAt").value(endsWith("Z"))).andExpect(jsonPath("$.createdAt").value(endsWith("Z"))).andExpect(jsonPath("$.updatedAt").value(endsWith("Z")));
    }

    @Test
    void preferredLanguageIsStoredAndUsedAsFallbackForAuthenticatedErrors() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(put("/api/account/language").with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "preferredLanguage": "pl"
                }
                """)).andExpect(status().isOk()).andExpect(jsonPath("$.preferredLanguage").value("pl")).andExpect(jsonPath("$.lastLoginAt").value(endsWith("Z"))).andExpect(jsonPath("$.createdAt").value(endsWith("Z"))).andExpect(jsonPath("$.updatedAt").value(endsWith("Z")));

        mockMvc.perform(post("/api/categories").with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "name": "Architecture"
                }
                """)).andExpect(status().isForbidden()).andExpect(jsonPath("$.title").value("Forbidden")).andExpect(jsonPath("$.status").value(403)).andExpect(jsonPath("$.detail").value("Category management requires the ADMIN role.")).andExpect(jsonPath("$.messageKey").value("error.request.forbidden")).andExpect(jsonPath("$.message").value("Nie masz uprawnien do wykonania tej operacji.")).andExpect(jsonPath("$.language").value("pl"));
    }

    @Test
    void updatePreferredLanguageWithInvalidCsrfReturnsDedicatedForbiddenProblem() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(put("/api/account/language").with(readerSession.unsafeWriteWithInvalidCsrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "preferredLanguage": "pl"
                }
                """)).andExpect(status().isForbidden()).andExpect(jsonPath("$.title").value("Invalid CSRF Token")).andExpect(jsonPath("$.messageKey").value("error.request.csrf_invalid"));
    }

    @Test
    void repeatedAuthenticatedRequestsRefreshLastLoginTimestamp() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession())).andExpect(status().isOk());

        UserAccount storedUser = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user").orElseThrow();
        storedUser.setLastLoginAt(Instant.now().minus(2, ChronoUnit.DAYS));
        userAccountRepository.saveAndFlush(storedUser);

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession())).andExpect(status().isOk());

        UserAccount refreshedUser = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user").orElseThrow();
        assertThat(refreshedUser.getLastLoginAt()).isAfter(Instant.now().minus(1, ChronoUnit.HOURS));
    }

    @Test
    void userOperationsPublishMetricsAndGauges() throws Exception {
        double createBefore = counterValue(USER_OPERATIONS, "operation", "create");
        double updatePreferenceBefore = counterValue(USER_OPERATIONS, "operation", "updatePreferredLanguage");
        BrowserSession readerSession = readerSession();
        BrowserSession adminSession = adminSession();

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession())).andExpect(status().isOk());
        mockMvc.perform(put("/api/account/language").with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "preferredLanguage": "fr"
                }
                """)).andExpect(status().isOk());
        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "name": "Architecture"
                }
                """)).andExpect(status().isCreated());

        assertThat(counterValue(USER_OPERATIONS, "operation", "create") - createBefore).isEqualTo(2.0d);
        assertThat(counterValue(USER_OPERATIONS, "operation", "updatePreferredLanguage") - updatePreferenceBefore).isEqualTo(1.0d);
        assertThat(gaugeValue(USER_TOTAL)).isEqualTo((double) userAccountRepository.count());
        assertThat(gaugeValue(ADMIN_TOTAL)).isEqualTo(1.0d);
    }

    private void clearCaches() {
        CacheTestSupport.clearCaches(cacheManager, List.of(CacheNames.CATEGORIES, CacheNames.CATEGORY_DIRECTORY));
    }

    private double counterValue(String meterName, String... tags) {
        io.micrometer.core.instrument.Counter counter = meterRegistry.find(meterName).tags(tags).counter();
        return counter == null ? 0.0d : counter.count();
    }

    private double gaugeValue(String meterName) {
        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find(meterName).gauge();
        assertThat(gauge).isNotNull();
        return gauge.value();
    }

    private BrowserSession adminSession() {
        return adminBrowserSession(sessionRepository);
    }

    private BrowserSession readerSession() {
        return authenticatedBrowserSession(sessionRepository, "reader-user");
    }
}
