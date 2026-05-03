package team.jit.technicalinterviewdemo.technical.security;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductionSecurityConfigurationValidator implements InitializingBean {

    private static final Pattern ADMIN_LOGIN_PATTERN =
            Pattern.compile("^[A-Za-z\\d](?:[A-Za-z\\d._@-]{0,126}[A-Za-z\\d])?$");
    private static final Pattern PROVIDER_ID_PATTERN =
            Pattern.compile("^[a-z\\d](?:[a-z\\d-]{0,48}[a-z\\d])?$");

    private final SecuritySettingsProperties securitySettingsProperties;
    private final Environment environment;

    @Override
    public void afterPropertiesSet() {
        validateRemovedSettings();
        if (!environment.acceptsProfiles(Profiles.of("prod"))) {
            return;
        }
        validateSessionContract();
        validateAdminLogins();
        validateForwardedHeaderStrategy();
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
            if (!ADMIN_LOGIN_PATTERN.matcher(login).matches()) {
                throw new IllegalStateException(
                        "ADMIN_LOGINS must contain comma-separated external login identifiers. Invalid value: " + login
                );
            }
        }
    }

    private void validateRemovedSettings() {
        String deprecatedDefaultProvider = firstNonBlank(
                environment.getProperty("OAUTH_DEFAULT_PROVIDER"),
                environment.getProperty("app.security.oauth.default-provider")
        );
        if (deprecatedDefaultProvider != null) {
            throw new IllegalStateException(
                    "OAUTH_DEFAULT_PROVIDER has been removed. Expose provider choices through GET /api/session loginProviders[]."
            );
        }
    }

    private void validateForwardedHeaderStrategy() {
        String forwardHeadersStrategy = environment.getProperty("server.forward-headers-strategy", "");
        if (!"framework".equalsIgnoreCase(forwardHeadersStrategy)) {
            throw new IllegalStateException(
                    "Prod profile requires server.forward-headers-strategy=framework."
            );
        }
    }

    private void validateOAuthSettings() {
        if (!environment.acceptsProfiles(Profiles.of("oauth"))) {
            return;
        }
        SecuritySettingsProperties.OAuth oauthSettings = securitySettingsProperties.getOAuth();
        Map<String, SecuritySettingsProperties.OAuth.Provider> configuredProviders = oauthSettings.configuredProviders();
        if (configuredProviders.isEmpty()) {
            throw new IllegalStateException(
                    "OAuth-enabled prod profile requires at least one configured identity provider."
            );
        }

        for (Map.Entry<String, SecuritySettingsProperties.OAuth.Provider> providerEntry : configuredProviders.entrySet()) {
            String registrationId = providerEntry.getKey();
            SecuritySettingsProperties.OAuth.Provider provider = providerEntry.getValue();
            validateProviderId(registrationId);
            validateProviderConfiguration(registrationId, provider);
        }

    }

    private void validateProviderId(String registrationId) {
        if (!PROVIDER_ID_PATTERN.matcher(registrationId).matches()) {
            throw new IllegalStateException(
                    "OAuth provider id must match [a-z0-9-] and start/end with alphanumeric. Invalid value: "
                            + registrationId
            );
        }
    }

    private void validateProviderConfiguration(
            String registrationId,
            SecuritySettingsProperties.OAuth.Provider provider
    ) {
        if (provider.getType() == null) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires a type of GITHUB or OIDC."
                            .formatted(registrationId)
            );
        }
        if (!provider.hasClientCredentials()) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires both client-id and client-secret."
                            .formatted(registrationId)
            );
        }
        switch (provider.getType()) {
            case GITHUB -> validateGithubProvider(registrationId, provider);
            case OIDC -> validateOidcProvider(registrationId, provider);
        }
    }

    private void validateGithubProvider(
            String registrationId,
            SecuritySettingsProperties.OAuth.Provider provider
    ) {
        if (!provider.normalizedIssuerUri().isBlank()) {
            throw new IllegalStateException(
                    "GitHub provider '%s' must not define issuer-uri."
                            .formatted(registrationId)
            );
        }
    }

    private void validateOidcProvider(
            String registrationId,
            SecuritySettingsProperties.OAuth.Provider provider
    ) {
        if (provider.normalizedIssuerUri().isBlank()) {
            throw new IllegalStateException(
                    "OIDC provider '%s' requires issuer-uri."
                            .formatted(registrationId)
            );
        }
        if (!provider.normalizedScope().contains("openid")) {
            throw new IllegalStateException(
                    "OIDC provider '%s' scope must include openid."
                            .formatted(registrationId)
            );
        }
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
