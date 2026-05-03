package team.jit.technicalinterviewdemo.technical.security;

import java.time.Duration;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProductionSecurityConfigurationValidator implements InitializingBean {

    private static final Pattern GITHUB_LOGIN_PATTERN =
            Pattern.compile("^[A-Za-z\\d](?:[A-Za-z\\d]|-(?=[A-Za-z\\d])){0,38}$");

    private final SecuritySettingsProperties securitySettingsProperties;
    private final Environment environment;

    @Override
    public void afterPropertiesSet() {
        validateSessionContract();
        validateAdminLogins();
        validateOAuthSettings();
    }

    private void validateSessionContract() {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        if (!session.isCookieSecure()) {
            throw new IllegalStateException(
                    "Prod profile requires SESSION_COOKIE_SECURE=true so authenticated browser sessions stay secure."
            );
        }
        if (session.getTimeout().compareTo(Duration.ofMinutes(5)) < 0) {
            throw new IllegalStateException(
                    "Prod profile requires server.servlet.session.timeout to be at least 5 minutes."
            );
        }
        if (!session.isCookieHttpOnly()) {
            throw new IllegalStateException(
                    "Prod profile requires server.servlet.session.cookie.http-only=true."
            );
        }
        if (!"lax".equalsIgnoreCase(session.getCookieSameSite())
                && !"strict".equalsIgnoreCase(session.getCookieSameSite())) {
            throw new IllegalStateException(
                    "Prod profile requires server.servlet.session.cookie.same-site to be lax or strict."
            );
        }
    }

    private void validateAdminLogins() {
        for (String login : securitySettingsProperties.normalizedAdminLogins()) {
            if (!GITHUB_LOGIN_PATTERN.matcher(login).matches()) {
                throw new IllegalStateException(
                        "ADMIN_LOGINS must contain comma-separated GitHub logins. Invalid value: " + login
                );
            }
        }
    }

    private void validateOAuthSettings() {
        if (!environment.acceptsProfiles(Profiles.of("oauth"))) {
            return;
        }
        SecuritySettingsProperties.OAuth.Github github = securitySettingsProperties.getOAuth().getGithub();
        if (github.getClientId().isBlank()) {
            throw new IllegalStateException(
                    "OAuth-enabled prod profile requires GITHUB_CLIENT_ID."
            );
        }
        if (github.getClientSecret().isBlank()) {
            throw new IllegalStateException(
                    "OAuth-enabled prod profile requires GITHUB_CLIENT_SECRET."
            );
        }
    }
}
