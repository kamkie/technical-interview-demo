package team.jit.technicalinterviewdemo.testing;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import jakarta.servlet.http.Cookie;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

public final class SecurityTestSupport {

    private SecurityTestSupport() {}

    public static RequestPostProcessor oauthUser() {
        return oauthUser("demo-user");
    }

    public static RequestPostProcessor oauthUser(String login) {
        return authentication(oauthAuthentication(login));
    }

    public static RequestPostProcessor adminOauthUser() {
        return oauthUser("admin-user");
    }

    public static <S extends Session> BrowserSession authenticatedBrowserSession(
            SessionRepository<S> sessionRepository, String login) {
        return browserSession(createAuthenticatedSession(sessionRepository, login), login);
    }

    public static <S extends Session> BrowserSession adminBrowserSession(SessionRepository<S> sessionRepository) {
        return authenticatedBrowserSession(sessionRepository, "admin-user");
    }

    public static BrowserSession browserSession(String sessionId, String login) {
        return new BrowserSession(sessionCookie(sessionId), csrfCookie(), oauthUser(login));
    }

    public static BrowserSession browserSession(String sessionId) {
        return browserSession(sessionId, "demo-user");
    }

    public static void setAuthenticatedUser(String login) {
        SecurityContextHolder.getContext().setAuthentication(oauthAuthentication(login));
    }

    public static void setAdminAuthenticatedUser() {
        setAuthenticatedUser("admin-user");
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    public static <S extends Session> String createAuthenticatedSession(
            SessionRepository<S> sessionRepository, String login) {
        S session = sessionRepository.createSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(oauthAuthentication(login)));
        sessionRepository.save(session);
        return session.getId();
    }

    public static Cookie sessionCookie(String sessionId) {
        String encodedSessionId = java.util.Base64.getEncoder()
                .encodeToString(sessionId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return new Cookie("technical-interview-demo-session", encodedSessionId);
    }

    private static Cookie csrfCookie() {
        return new Cookie(SameSiteCsrfContract.COOKIE_NAME, UUID.randomUUID().toString());
    }

    private static OAuth2AuthenticationToken oauthAuthentication(String login) {
        DefaultOAuth2User oauth2User = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"),
                Map.of(
                        "login", login,
                        "name", login + " display",
                        "email", login + "@example.test"),
                "login");
        return new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github");
    }

    public record BrowserSession(Cookie sessionCookie, Cookie csrfCookie, RequestPostProcessor authentication) {

        public RequestPostProcessor authenticatedSession() {
            return request -> {
                authentication.postProcessRequest(request);
                request.setCookies(sessionCookie);
                return request;
            };
        }

        public RequestPostProcessor unsafeWrite() {
            return request -> {
                authentication.postProcessRequest(request);
                request.setCookies(sessionCookie, csrfCookie);
                request.addHeader(SameSiteCsrfContract.HEADER_NAME, csrfCookie.getValue());
                return request;
            };
        }

        public RequestPostProcessor unsafeWriteWithInvalidCsrf() {
            return request -> {
                authentication.postProcessRequest(request);
                request.setCookies(sessionCookie, csrfCookie);
                request.addHeader(SameSiteCsrfContract.HEADER_NAME, csrfCookie.getValue() + "-invalid");
                return request;
            };
        }
    }
}
