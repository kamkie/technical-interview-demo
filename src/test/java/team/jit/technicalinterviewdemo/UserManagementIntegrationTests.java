package team.jit.technicalinterviewdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.SecurityTestSupport.oauthUser;

import io.micrometer.core.instrument.MeterRegistry;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.business.user.UserAccount;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.business.user.UserRole;

@TestcontainersTest
@SpringBootTest
@AutoConfigureMockMvc
class UserManagementIntegrationTests {

    private static final String USER_OPERATIONS = "technical.interview.demo.users.operations";
    private static final String USER_TOTAL = "technical.interview.demo.users.total";
    private static final String ADMIN_TOTAL = "technical.interview.demo.users.admin.total";

    @Autowired
    private MockMvc mockMvc;

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
        mockMvc.perform(post("/api/books")
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022
                                }
                                """))
                .andExpect(status().isCreated());

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user")
                .orElseThrow();

        assertThat(userAccount.getRoles()).containsExactly(UserRole.USER);
        assertThat(userAccount.getDisplayName()).isEqualTo("reader-user display");
        assertThat(userAccount.getEmail()).isEqualTo("reader-user@example.test");
        assertThat(userAccount.getLastLoginAt()).isNotNull();
    }

    @Test
    void adminLoginGetsAdminRoleAndCanManageCategories() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isCreated());

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin("github", "admin-user")
                .orElseThrow();

        assertThat(userAccount.getRoles()).containsExactlyInAnyOrder(UserRole.USER, UserRole.ADMIN);
    }

    @Test
    void currentUserEndpointReturnsPersistedProfile() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(oauthUser("reader-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("github"))
                .andExpect(jsonPath("$.login").value("reader-user"))
                .andExpect(jsonPath("$.displayName").value("reader-user display"))
                .andExpect(jsonPath("$.email").value("reader-user@example.test"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void preferredLanguageIsStoredAndUsedAsFallbackForAuthenticatedErrors() throws Exception {
        mockMvc.perform(put("/api/users/me/preferred-language")
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "preferredLanguage": "pl"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredLanguage").value("pl"));

        mockMvc.perform(post("/api/categories")
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.messageKey").value("error.request.forbidden"))
                .andExpect(jsonPath("$.message").value("Nie masz uprawnien do wykonania tej operacji."))
                .andExpect(jsonPath("$.language").value("pl"));
    }

    @Test
    void repeatedAuthenticatedRequestsRefreshLastLoginTimestamp() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(oauthUser("reader-user")))
                .andExpect(status().isOk());

        UserAccount storedUser = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user")
                .orElseThrow();
        storedUser.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(2));
        userAccountRepository.saveAndFlush(storedUser);

        mockMvc.perform(get("/api/users/me")
                        .with(oauthUser("reader-user")))
                .andExpect(status().isOk());

        UserAccount refreshedUser = userAccountRepository.findByProviderAndExternalLogin("github", "reader-user")
                .orElseThrow();
        assertThat(refreshedUser.getLastLoginAt()).isAfter(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));
    }

    @Test
    void userOperationsPublishMetricsAndGauges() throws Exception {
        double createBefore = counterValue(USER_OPERATIONS, "operation", "create");
        double updatePreferenceBefore = counterValue(USER_OPERATIONS, "operation", "updatePreferredLanguage");

        mockMvc.perform(get("/api/users/me")
                        .with(oauthUser("reader-user")))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/users/me/preferred-language")
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "preferredLanguage": "fr"
                                }
                                """))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/categories")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Architecture"
                                }
                                """))
                .andExpect(status().isCreated());

        assertThat(counterValue(USER_OPERATIONS, "operation", "create") - createBefore).isEqualTo(2.0d);
        assertThat(counterValue(USER_OPERATIONS, "operation", "updatePreferredLanguage") - updatePreferenceBefore).isEqualTo(1.0d);
        assertThat(gaugeValue(USER_TOTAL)).isEqualTo((double) userAccountRepository.count());
        assertThat(gaugeValue(ADMIN_TOTAL)).isEqualTo(1.0d);
    }

    private void clearCaches() {
        for (String cacheName : List.of(CacheNames.CATEGORIES, CacheNames.CATEGORY_DIRECTORY)) {
            cacheManager.getCache(cacheName).clear();
        }
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
}

