package team.jit.technicalinterviewdemo.technical.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
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
import org.springframework.http.HttpHeaders;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
@ActiveProfiles(value = {"test", "oauth"}, inheritProfiles = false)
@TestPropertySource(properties = {
        "app.security.oauth.providers.github.client-id=test-client-id",
        "app.security.oauth.providers.github.client-secret=test-client-secret"
})
class SessionApiOauthIntegrationTests extends AbstractMockMvcIntegrationTest {

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
                .andExpect(jsonPath("$.csrf.enabled").value(false));
    }

    @Test
    void sessionEndpointReturnsAuthenticatedStateForJdbcBackedSession() throws Exception {
        String sessionId = createAuthenticatedSession("reader-user");

        mockMvc.perform(get("/api/session")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.accountPath").value("/api/account"))
                .andExpect(jsonPath("$.loginProviders.length()").value(1))
                .andExpect(jsonPath("$.loginProviders[0].authorizationPath")
                        .value("/api/session/oauth2/authorization/github"))
                .andExpect(jsonPath("$.logoutPath").value("/api/session/logout"));
    }

    @Test
    void oauthAuthorizationEndpointUsesRelativeApiSessionPathInLocalTestProfile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/session/oauth2/authorization/github"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertThat(location).startsWith("https://github.com/login/oauth/authorize?");
        assertThat(UriComponentsBuilder.fromUriString(location).build(true).getQueryParams().getFirst("redirect_uri"))
                .isEqualTo("http://localhost/api/session/login/oauth2/code/github");
    }

    @Test
    void logoutEndpointInvalidatesJdbcBackedSessionAndClearsCookie() throws Exception {
        String sessionId = createAuthenticatedSession("reader-user");

        mockMvc.perform(post("/api/session/logout")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        allOf(
                                containsString("technical-interview-demo-session="),
                                containsString("Max-Age=0"),
                                containsString("HttpOnly"),
                                containsString("SameSite=lax")
                        )
                ));

        assertThat(httpSessionRepository().findById(sessionId)).isNull();

        mockMvc.perform(get("/api/account")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"));

        mockMvc.perform(get("/api/session")
                        .cookie(sessionCookie(sessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
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
