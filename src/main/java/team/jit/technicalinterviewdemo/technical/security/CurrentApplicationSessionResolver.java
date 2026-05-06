package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentApplicationSessionResolver {

    private final Environment environment;
    private final SessionRepository<? extends Session> sessionRepository;

    public boolean hasAuthenticatedSession(HttpServletRequest request) {
        return currentSession(request)
                .filter(this::hasAuthenticatedSecurityContext)
                .isPresent();
    }

    public Optional<String> currentSessionId(HttpServletRequest request) {
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

    private Optional<Session> currentSession(HttpServletRequest request) {
        return currentSessionId(request)
                .map(sessionRepository::findById);
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

    private String sessionCookieName() {
        return environment.getProperty("server.servlet.session.cookie.name", "SESSION");
    }
}
