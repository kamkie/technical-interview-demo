package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.web.servlet.MockMvc;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.UserAccount;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

@MockMvcIntegrationSpringBootTest
class SecurityIntegrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private AuthenticationSuccessHandler oauthAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler oauthAuthenticationFailureHandler;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private SessionRepository<Session> httpSessionRepository() {
        return (SessionRepository) sessionRepository;
    }

    @BeforeEach
    void clearState() {
        jdbcTemplate.update("DELETE FROM SPRING_SESSION_ATTRIBUTES");
        jdbcTemplate.update("DELETE FROM SPRING_SESSION");
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void jdbcSessionRepositoryStoresOAuth2SecurityContext() {
        SecurityContext securityContext = new SecurityContextImpl(authentication("demo-user"));
        Session session = httpSessionRepository().createSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
        httpSessionRepository().save(session);

        Integer sessions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION WHERE SESSION_ID = ?", Integer.class, session.getId());
        Integer attributes =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SPRING_SESSION_ATTRIBUTES", Integer.class);
        Session storedSession = httpSessionRepository().findById(session.getId());

        assertThat(sessions).isEqualTo(1);
        assertThat(attributes).isGreaterThan(0);
        assertThat(storedSession).isNotNull();
        Object storedSecurityContext =
                storedSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertThat(storedSecurityContext).isNotNull();
    }

    @Test
    void springSessionRegistrySupportsSingleSessionRejectionForOauthLogins() {
        OAuth2AuthenticationToken authentication = authentication("demo-user");
        Session existingSession = httpSessionRepository().createSession();
        existingSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(authentication));
        httpSessionRepository().save(existingSession);

        ConcurrentSessionControlAuthenticationStrategy strategy =
                new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        strategy.setMaximumSessions(1);
        strategy.setExceptionIfMaximumExceeded(true);

        MockHttpServletRequest secondLoginRequest = new MockHttpServletRequest();
        secondLoginRequest.setSession(new MockHttpSession(null, "second-session"));

        assertThat(sessionRegistry.getAllSessions(authentication.getPrincipal(), false))
                .hasSize(1);
        assertThatThrownBy(() ->
                        strategy.onAuthentication(authentication, secondLoginRequest, new MockHttpServletResponse()))
                .isInstanceOf(SessionAuthenticationException.class)
                .hasMessageContaining("Maximum sessions of 1");
    }

    @Test
    void oauthSuccessHandlerAlwaysRedirectsToSharedFrontendRoot() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());
        request.getSession().setAttribute("SPRING_SECURITY_SAVED_REQUEST", "/api/account");
        MockHttpServletResponse response = new MockHttpServletResponse();

        oauthAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication("demo-user"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/");
        UserAccount userAccount = userAccountRepository
                .findByProviderAndExternalLogin("github", "demo-user")
                .orElseThrow();
        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getTargetId()).isEqualTo(userAccount.getId());
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.LOGIN_SUCCESS);
        assertThat(auditLog.getActorLogin()).isEqualTo("demo-user");
        assertThat(auditLog.getSummary()).isEqualTo("Successful OAuth login for 'demo-user'.");
        assertThat(auditLog.getDetails()).containsEntry("provider", "github").containsEntry("login", "demo-user");
    }

    @Test
    void oauthFailureHandlerAlwaysRedirectsToLoginFailedMarker() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/session/login/oauth2/code/github");
        MockHttpServletResponse response = new MockHttpServletResponse();

        oauthAuthenticationFailureHandler.onAuthenticationFailure(
                request, response, new OAuth2AuthenticationException(new OAuth2Error("invalid_token")));

        assertThat(response.getRedirectedUrl()).isEqualTo("/?login=failed");
        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getTargetId()).isNull();
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.LOGIN_FAILURE);
        assertThat(auditLog.getActorLogin()).isNull();
        assertThat(auditLog.getSummary()).isEqualTo("OAuth login failed.");
        assertThat(auditLog.getDetails())
                .containsEntry("provider", "github")
                .containsEntry("failureType", "oauth_authentication_failure")
                .containsEntry("errorCode", "invalid_token");
    }

    @Test
    void oauthFailureHandlerRecordsSessionRejectionAuditEntry() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/session/login/oauth2/code/github");
        MockHttpServletResponse response = new MockHttpServletResponse();

        oauthAuthenticationFailureHandler.onAuthenticationFailure(
                request, response, new SessionAuthenticationException("Maximum sessions of 1 exceeded"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/?login=failed");
        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getTargetId()).isNull();
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.SESSION_REJECTION);
        assertThat(auditLog.getActorLogin()).isNull();
        assertThat(auditLog.getSummary())
                .isEqualTo("Rejected OAuth login because the concurrent session limit was reached.");
        assertThat(auditLog.getDetails())
                .containsEntry("provider", "github")
                .containsEntry("failureType", "maximum_sessions_exceeded");
        assertThat(auditLog.getDetails()).doesNotContainKey("errorCode");
    }

    @Test
    void blockedAccountSignInIsRejectedWithoutSyncOrLoginSuccess() throws Exception {
        UserAccount blockedUser = persistBlockedUser("blocked-user");
        Instant lastLoginBefore = blockedUser.getLastLoginAt();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        oauthAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication("blocked-user"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/?login=failed");
        assertThat(session.isInvalid()).isTrue();

        UserAccount unchangedUser = userAccountRepository
                .findByProviderAndExternalLogin("github", "blocked-user")
                .orElseThrow();
        assertThat(unchangedUser.getLastLoginAt()).isEqualTo(lastLoginBefore);
        assertThat(unchangedUser.getDisplayName()).isEqualTo("Blocked User");

        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getTargetId()).isEqualTo(unchangedUser.getId());
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.LOGIN_FAILURE);
        assertThat(auditLog.getDetails())
                .containsEntry("failureReason", "account_blocked")
                .containsEntry("provider", "github")
                .containsEntry("login", "blocked-user");
    }

    @Test
    void blockedAccountActiveSessionIsRejectedDespiteSessionSyncCache() throws Exception {
        BrowserSession readerSession = authenticatedBrowserSession(sessionRepository, "reader-user");
        // First request populates the per-session synchronization shortcut attribute.
        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession()))
                .andExpect(status().isOk());

        UserAccount readerUser = userAccountRepository
                .findByProviderAndExternalLogin("github", "reader-user")
                .orElseThrow();
        readerUser.block(null, "Blocked between requests.");
        userAccountRepository.saveAndFlush(readerUser);
        auditLogRepository.deleteAll();
        String sessionId = decodeSessionId(readerSession);
        assertThat(sessionRepository.findById(sessionId)).isNotNull();

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value(401));

        assertThat(sessionRepository.findById(sessionId)).isNull();
        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog auditLog = auditLogRepository.findAll().getFirst();
        assertThat(auditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(auditLog.getTargetId()).isEqualTo(readerUser.getId());
        assertThat(auditLog.getAction()).isEqualTo(AuditAction.SESSION_REJECTION);
        assertThat(auditLog.getActorLogin()).isEqualTo("reader-user");
        assertThat(auditLog.getDetails())
                .containsEntry("failureReason", "account_blocked")
                .containsEntry("provider", "github")
                .containsEntry("login", "reader-user");
    }

    @Test
    void unblockedAccountKeepsSessionContinuityAcrossRequests() throws Exception {
        BrowserSession readerSession = authenticatedBrowserSession(sessionRepository, "reader-user");

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession()))
                .andExpect(status().isOk());

        String sessionId = decodeSessionId(readerSession);
        assertThat(sessionRepository.findById(sessionId)).isNotNull();
    }

    private UserAccount persistBlockedUser(String login) {
        UserAccount userAccount = new UserAccount(
                "github",
                login,
                "Blocked User",
                login + "@example.test",
                null,
                Instant.parse("2024-01-01T00:00:00Z"),
                Set.of(UserRole.USER));
        userAccount.block(null, "Blocked for testing.");
        return userAccountRepository.saveAndFlush(userAccount);
    }

    private String decodeSessionId(BrowserSession browserSession) {
        return new String(
                Base64.getDecoder().decode(browserSession.sessionCookie().getValue()), StandardCharsets.UTF_8);
    }

    private OAuth2AuthenticationToken authentication(String login) {
        DefaultOAuth2User oauth2User =
                new DefaultOAuth2User(AuthorityUtils.createAuthorityList("ROLE_USER"), Map.of("login", login), "login");
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github");
    }
}
