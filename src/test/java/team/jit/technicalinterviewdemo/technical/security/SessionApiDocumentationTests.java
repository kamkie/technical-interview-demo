package team.jit.technicalinterviewdemo.technical.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;

@RestDocsIntegrationSpringBootTest
@ActiveProfiles(value = {"test", "oauth"}, inheritProfiles = false)
@TestPropertySource(properties = {
        "app.security.oauth.default-provider=github",
        "app.security.oauth.providers.github.client-id=test-client-id",
        "app.security.oauth.providers.github.client-secret=test-client-secret"
})
class SessionApiDocumentationTests extends AbstractDocumentationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private SessionRepository<Session> httpSessionRepository() {
        return (SessionRepository) sessionRepository;
    }

    @BeforeEach
    void clearSessions() {
        jdbcTemplate.update("DELETE FROM SPRING_SESSION_ATTRIBUTES");
        jdbcTemplate.update("DELETE FROM SPRING_SESSION");
    }

    @Test
    void documentGetSessionEndpointForAnonymousBrowserState() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.loginPath").value("/oauth2/authorization/github"))
                .andDo(documentEndpoint(
                        "session/get-session",
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("authenticated").description("Whether the current browser request is backed by an authenticated application session."),
                                fieldWithPath("accountPath").description("Endpoint path for the authenticated persisted-account resource."),
                                fieldWithPath("loginPath").description("Interactive login bootstrap path for the current runtime, or an empty string when oauth is inactive."),
                                fieldWithPath("logoutPath").description("Same-site logout endpoint path."),
                                fieldWithPath("sessionCookie.name").description("Session cookie name expected by protected operations."),
                                fieldWithPath("sessionCookie.httpOnly").description("Whether the session cookie is HTTP-only."),
                                fieldWithPath("sessionCookie.sameSite").description("Session cookie SameSite mode."),
                                fieldWithPath("sessionCookie.secure").description("Whether the session cookie requires HTTPS."),
                                fieldWithPath("csrf.enabled").description("Whether CSRF protection is currently enabled for browser writes.")
                        )
                ));
    }

    @Test
    void documentGetSessionEndpointForAuthenticatedBrowserState() throws Exception {
        String sessionId = createAuthenticatedSession("reader-user");

        mockMvc.perform(get("/api/session")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andDo(documentEndpoint(
                        "session/get-session-authenticated",
                        responseHeaders(commonResponseHeaders()),
                        responseFields(
                                fieldWithPath("authenticated").description("Whether the current browser request is backed by an authenticated application session."),
                                fieldWithPath("accountPath").description("Endpoint path for the authenticated persisted-account resource."),
                                fieldWithPath("loginPath").description("Interactive login bootstrap path for the current runtime, or an empty string when oauth is inactive."),
                                fieldWithPath("logoutPath").description("Same-site logout endpoint path."),
                                fieldWithPath("sessionCookie.name").description("Session cookie name expected by protected operations."),
                                fieldWithPath("sessionCookie.httpOnly").description("Whether the session cookie is HTTP-only."),
                                fieldWithPath("sessionCookie.sameSite").description("Session cookie SameSite mode."),
                                fieldWithPath("sessionCookie.secure").description("Whether the session cookie requires HTTPS."),
                                fieldWithPath("csrf.enabled").description("Whether CSRF protection is currently enabled for browser writes.")
                        )
                ));
    }

    @Test
    void documentLogoutEndpoint() throws Exception {
        String sessionId = createAuthenticatedSession("reader-user");

        mockMvc.perform(post("/api/session/logout")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")))
                .andDo(documentEndpoint(
                        "session/post-session-logout",
                        responseHeaders(
                                headerWithName("Set-Cookie").description("Clears the session cookie at the application root path."),
                                headerWithName("X-Request-Id").description("Request identifier returned on every public endpoint."),
                                headerWithName("traceparent").description("Trace context header returned when tracing is active.")
                        )
                ));
    }

    private String createAuthenticatedSession(String login) {
        Session session = httpSessionRepository().createSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(authentication(login))
        );
        httpSessionRepository().save(session);
        return session.getId();
    }

    private Cookie sessionCookie(String sessionId) {
        String encodedSessionId = Base64.getEncoder().encodeToString(sessionId.getBytes(StandardCharsets.UTF_8));
        return new Cookie("technical-interview-demo-session", encodedSessionId);
    }

    private OAuth2AuthenticationToken authentication(String login) {
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of(
                        "login", login,
                        "name", login + " display",
                        "email", login + "@example.test"
                ),
                "login"
        );
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github");
    }
}
