package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

    static final String ACCOUNT_PATH = "/api/account";
    static final String LOGOUT_PATH = "/api/session/logout";

    private final SecuritySettingsProperties securitySettingsProperties;
    private final Environment environment;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository;
    private final CurrentApplicationSessionResolver currentApplicationSessionResolver;
    private final SessionRepository<? extends Session> sessionRepository;
    private final CsrfTokenRepository csrfTokenRepository;

    public SessionResponse currentSession(HttpServletRequest request, HttpServletResponse response) {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        csrfTokenRepository.loadDeferredToken(request, response).get();
        return new SessionResponse(
                currentApplicationSessionResolver.hasAuthenticatedSession(request),
                ACCOUNT_PATH,
                loginProviders(),
                LOGOUT_PATH,
                new SessionResponse.SessionCookie(
                        sessionCookieName(),
                        session.isCookieHttpOnly(),
                        session.getCookieSameSite(),
                        session.isCookieSecure()
                ),
                new SessionResponse.Csrf(
                        true,
                        SameSiteCsrfContract.COOKIE_NAME,
                        SameSiteCsrfContract.HEADER_NAME
                )
        );
    }

    public void logoutCurrentSession(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> sessionId = currentApplicationSessionResolver.currentSessionId(request);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionId.ifPresent(sessionRepository::deleteById);
        SecurityContextHolder.clearContext();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredSessionCookie().toString());
        csrfTokenRepository.saveToken(null, request, response);
    }

    private List<SessionResponse.LoginProvider> loginProviders() {
        if (!environment.acceptsProfiles(Profiles.of("oauth"))) {
            return List.of();
        }

        ClientRegistrationRepository registrationRepository = clientRegistrationRepository.getIfAvailable();
        if (!(registrationRepository instanceof Iterable<?> iterableRegistrationRepository)) {
            return List.of();
        }

        List<SessionResponse.LoginProvider> loginProviders = new ArrayList<>();
        for (Object registration : iterableRegistrationRepository) {
            if (registration instanceof ClientRegistration clientRegistration) {
                loginProviders.add(new SessionResponse.LoginProvider(
                        clientRegistration.getRegistrationId(),
                        clientRegistration.getClientName(),
                        SecuritySettingsProperties.OAuth.authorizationPath(clientRegistration.getRegistrationId())
                ));
            }
        }
        return List.copyOf(loginProviders);
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
