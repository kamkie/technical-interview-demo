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
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.browserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.createAuthenticatedSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.sessionCookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import team.jit.technicalinterviewdemo.testing.AbstractDocumentationIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RestDocsIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

@RestDocsIntegrationSpringBootTest
@ActiveProfiles(
        value = {"test", "oauth"},
        inheritProfiles = false)
@TestPropertySource(
        properties = {
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
                .andExpect(jsonPath("$.loginProviders.length()").value(1))
                .andDo(documentEndpoint(
                        "session/get-session",
                        responseHeaders(commonResponseHeaders(headerWithName("Set-Cookie")
                                .description("Bootstraps or refreshes the readable CSRF cookie used by the"
                                        + " same-site browser UI."))),
                        responseFields(
                                fieldWithPath("authenticated")
                                        .description("Whether the current browser request is backed by an authenticated"
                                                + " application session."),
                                fieldWithPath("accountPath")
                                        .description("Endpoint path for the authenticated persisted-account resource."),
                                fieldWithPath("loginProviders[].registrationId")
                                        .description("Configured OAuth client registration id."),
                                fieldWithPath("loginProviders[].clientName")
                                        .description(
                                                "Display name exposed by the configured OAuth client registration."),
                                fieldWithPath("loginProviders[].authorizationPath")
                                        .description(
                                                "Relative same-site authorization bootstrap path for the configured"
                                                        + " provider."),
                                fieldWithPath("logoutPath").description("Same-site logout endpoint path."),
                                fieldWithPath("sessionCookie.name")
                                        .description("Session cookie name expected by protected operations."),
                                fieldWithPath("sessionCookie.httpOnly")
                                        .description("Whether the session cookie is HTTP-only."),
                                fieldWithPath("sessionCookie.sameSite").description("Session cookie SameSite mode."),
                                fieldWithPath("sessionCookie.secure")
                                        .description("Whether the session cookie requires HTTPS."),
                                fieldWithPath("csrf.enabled")
                                        .description(
                                                "Whether CSRF protection is currently enabled for browser writes."),
                                fieldWithPath("csrf.cookieName")
                                        .description("Readable CSRF cookie name mirrored by the browser UI."),
                                fieldWithPath("csrf.headerName")
                                        .description("Request header name required on unsafe browser writes."))));
    }

    @Test
    void documentGetSessionEndpointForAuthenticatedBrowserState() throws Exception {
        String sessionId = createAuthenticatedSession(httpSessionRepository(), "reader-user");

        mockMvc.perform(get("/api/session").cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.loginProviders.length()").value(1))
                .andDo(documentEndpoint(
                        "session/get-session-authenticated",
                        responseHeaders(commonResponseHeaders(headerWithName("Set-Cookie")
                                .description("Bootstraps or refreshes the readable CSRF cookie for the"
                                        + " current authenticated browser session."))),
                        responseFields(
                                fieldWithPath("authenticated")
                                        .description("Whether the current browser request is backed by an authenticated"
                                                + " application session."),
                                fieldWithPath("accountPath")
                                        .description("Endpoint path for the authenticated persisted-account resource."),
                                fieldWithPath("loginProviders[].registrationId")
                                        .description("Configured OAuth client registration id."),
                                fieldWithPath("loginProviders[].clientName")
                                        .description(
                                                "Display name exposed by the configured OAuth client registration."),
                                fieldWithPath("loginProviders[].authorizationPath")
                                        .description(
                                                "Relative same-site authorization bootstrap path for the configured"
                                                        + " provider."),
                                fieldWithPath("logoutPath").description("Same-site logout endpoint path."),
                                fieldWithPath("sessionCookie.name")
                                        .description("Session cookie name expected by protected operations."),
                                fieldWithPath("sessionCookie.httpOnly")
                                        .description("Whether the session cookie is HTTP-only."),
                                fieldWithPath("sessionCookie.sameSite").description("Session cookie SameSite mode."),
                                fieldWithPath("sessionCookie.secure")
                                        .description("Whether the session cookie requires HTTPS."),
                                fieldWithPath("csrf.enabled")
                                        .description(
                                                "Whether CSRF protection is currently enabled for browser writes."),
                                fieldWithPath("csrf.cookieName")
                                        .description("Readable CSRF cookie name mirrored by the browser UI."),
                                fieldWithPath("csrf.headerName")
                                        .description("Request header name required on unsafe browser writes."))));
    }

    @Test
    void documentLogoutEndpoint() throws Exception {
        String sessionId = createAuthenticatedSession(httpSessionRepository(), "reader-user");
        BrowserSession browserSession = browserSession(sessionId, "reader-user");

        mockMvc.perform(post("/api/session/logout").with(browserSession.unsafeWrite()))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")))
                .andDo(documentEndpoint(
                        "session/post-session-logout",
                        responseHeaders(commonResponseHeaders(headerWithName("Set-Cookie")
                                .description("Clears both the session cookie and the readable CSRF"
                                        + " cookie at the application root path.")))));
    }
}
