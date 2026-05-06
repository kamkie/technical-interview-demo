package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.browserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.createAuthenticatedSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.sessionCookie;

@MockMvcIntegrationSpringBootTest
@ActiveProfiles(
        value = {"test", "oauth"},
        inheritProfiles = false)
@TestPropertySource(
        properties = {
            "app.security.oauth.providers.github.client-id=test-client-id",
            "app.security.oauth.providers.github.client-secret=test-client-secret"
        })
class SessionApiOauthIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private SessionRepository<Session> httpSessionRepository() {
        return (SessionRepository) sessionRepository;
    }

    @BeforeEach
    void clearSessions() {
        jdbcTemplate.update("DELETE FROM SPRING_SESSION_ATTRIBUTES");
        jdbcTemplate.update("DELETE FROM SPRING_SESSION");
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void sessionEndpointReturnsAnonymousBootstrapPathWhenOauthProfileIsActive() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.loginProviders.length()").value(1))
                .andExpect(jsonPath("$.loginProviders[0].registrationId").value("github"))
                .andExpect(jsonPath("$.loginProviders[0].clientName").value("GitHub"))
                .andExpect(jsonPath("$.loginProviders[0].authorizationPath")
                        .value("/api/session/oauth2/authorization/github"))
                .andExpect(jsonPath("$.loginPath").doesNotExist())
                .andExpect(jsonPath("$.logoutPath").value("/api/session/logout"))
                .andExpect(jsonPath("$.sessionCookie.name").value("technical-interview-demo-session"))
                .andExpect(jsonPath("$.sessionCookie.sameSite").value("lax"))
                .andExpect(jsonPath("$.csrf.enabled").value(true))
                .andExpect(jsonPath("$.csrf.cookieName").value("XSRF-TOKEN"))
                .andExpect(jsonPath("$.csrf.headerName").value("X-XSRF-TOKEN"))
                .andExpect(header().string(
                                HttpHeaders.SET_COOKIE,
                                allOf(
                                        containsString("XSRF-TOKEN="),
                                        containsString("Path=/"),
                                        not(containsString("HttpOnly")))));
    }

    @Test
    void sessionEndpointReturnsAuthenticatedStateForJdbcBackedSession() throws Exception {
        String sessionId = createAuthenticatedSession(httpSessionRepository(), "reader-user");

        mockMvc.perform(get("/api/session").cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.accountPath").value("/api/account"))
                .andExpect(jsonPath("$.loginProviders.length()").value(1))
                .andExpect(jsonPath("$.loginProviders[0].authorizationPath")
                        .value("/api/session/oauth2/authorization/github"))
                .andExpect(jsonPath("$.logoutPath").value("/api/session/logout"))
                .andExpect(jsonPath("$.csrf.enabled").value(true))
                .andExpect(jsonPath("$.csrf.cookieName").value("XSRF-TOKEN"))
                .andExpect(jsonPath("$.csrf.headerName").value("X-XSRF-TOKEN"))
                .andExpect(header().string(
                                HttpHeaders.SET_COOKIE,
                                allOf(
                                        containsString("XSRF-TOKEN="),
                                        containsString("Path=/"),
                                        not(containsString("HttpOnly")))));
    }

    @Test
    void oauthAuthorizationEndpointUsesRelativeApiSessionPathInLocalTestProfile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/session/oauth2/authorization/github"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).startsWith("https://github.com/login/oauth/authorize?");
        assertThat(UriComponentsBuilder.fromUriString(location)
                        .build(true)
                        .getQueryParams()
                        .getFirst("redirect_uri"))
                .isEqualTo("http://localhost/api/session/login/oauth2/code/github");
    }

    @Test
    void logoutEndpointInvalidatesJdbcBackedSessionAndClearsCookie() throws Exception {
        String sessionId = createAuthenticatedSession(httpSessionRepository(), "reader-user");
        BrowserSession browserSession = browserSession(sessionId, "reader-user");

        mockMvc.perform(get("/api/account").with(browserSession.authenticatedSession()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/session/logout").with(browserSession.unsafeWrite()))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues(
                                HttpHeaders.SET_COOKIE,
                                allOf(
                                        hasItem(allOf(
                                                containsString("technical-interview-demo-session="),
                                                containsString("Max-Age=0"),
                                                containsString("HttpOnly"))),
                                        hasItem(allOf(
                                                containsString("XSRF-TOKEN="),
                                                containsString("Max-Age=0"),
                                                not(containsString("HttpOnly")))))));

        assertThat(httpSessionRepository().findById(sessionId)).isNull();

        mockMvc.perform(get("/api/account").cookie(sessionCookie(sessionId)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"));

        mockMvc.perform(get("/api/session").cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.LOGOUT);
        assertThat(auditLog.getActorLogin()).isEqualTo("reader-user");
        assertThat(auditLog.getDetails()).containsEntry("provider", "github").containsEntry("login", "reader-user");
    }

    @Test
    void logoutEndpointRequiresCsrfForAuthenticatedSession() throws Exception {
        String sessionId = createAuthenticatedSession(httpSessionRepository(), "reader-user");
        BrowserSession browserSession = browserSession(sessionId, "reader-user");

        mockMvc.perform(post("/api/session/logout").with(browserSession.authenticatedSession()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Invalid CSRF Token"))
                .andExpect(jsonPath("$.detail").value("A valid CSRF token is required to perform this operation."))
                .andExpect(jsonPath("$.messageKey").value("error.request.csrf_invalid"));

        assertThat(httpSessionRepository().findById(sessionId)).isNotNull();
    }
}
