package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;

@Service
@RequiredArgsConstructor
public class SessionService {

    static final String ACCOUNT_PATH = "/api/account";
    static final String LOGOUT_PATH = "/api/session/logout";

    private final SecuritySettingsProperties securitySettingsProperties;
    private final Environment environment;
    private final CurrentUserAccountService currentUserAccountService;
    private final SessionRepository<? extends Session> sessionRepository;

    public SessionResponse currentSession(HttpServletRequest request) {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        return new SessionResponse(
                isAuthenticated(request),
                ACCOUNT_PATH,
                oauthLoginPath(),
                LOGOUT_PATH,
                new SessionResponse.SessionCookie(
                        sessionCookieName(),
                        session.isCookieHttpOnly(),
                        session.getCookieSameSite(),
                        session.isCookieSecure()
                ),
                new SessionResponse.Csrf(false)
        );
    }

    public ResponseCookie logoutCurrentSession(HttpServletRequest request) {
        Optional<String> sessionId = sessionIdFrom(request);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionId.ifPresent(sessionRepository::deleteById);
        SecurityContextHolder.clearContext();
        return expiredSessionCookie();
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        if (currentUserAccountService.currentAuthenticatedUserKey().isPresent()) {
            return true;
        }

        return sessionIdFrom(request)
                .map(sessionRepository::findById)
                .filter(this::hasAuthenticatedSecurityContext)
                .isPresent();
    }

    private boolean hasAuthenticatedSecurityContext(Session session) {
        Object storedSecurityContext =
                session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (!(storedSecurityContext instanceof SecurityContext securityContext)) {
            return false;
        }

        Authentication authentication = securityContext.getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    private Optional<String> sessionIdFrom(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> sessionCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .map(this::decodeSessionId)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private Optional<String> decodeSessionId(String encodedSessionId) {
        if (encodedSessionId == null || encodedSessionId.isBlank()) {
            return Optional.empty();
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encodedSessionId);
            return Optional.of(new String(decoded, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private String oauthLoginPath() {
        if (!environment.acceptsProfiles(Profiles.of("oauth"))) {
            return "";
        }
        return securitySettingsProperties.getOAuth()
                .resolvedLoginPath()
                .orElse("/login");
    }

    private String sessionCookieName() {
        return environment.getProperty("server.servlet.session.cookie.name", "SESSION");
    }

    private ResponseCookie expiredSessionCookie() {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from(sessionCookieName(), "")
                .path("/")
                .httpOnly(session.isCookieHttpOnly())
                .secure(session.isCookieSecure())
                .maxAge(Duration.ZERO);

        if (session.getCookieSameSite() != null && !session.getCookieSameSite().isBlank()) {
            cookieBuilder.sameSite(session.getCookieSameSite());
        }
        return cookieBuilder.build();
    }
}
