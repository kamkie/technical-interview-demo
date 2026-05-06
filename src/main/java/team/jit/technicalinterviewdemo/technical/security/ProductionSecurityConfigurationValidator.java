package team.jit.technicalinterviewdemo.technical.security;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

@Component
@RequiredArgsConstructor
public class ProductionSecurityConfigurationValidator implements InitializingBean {

    private static final Pattern EXTERNAL_LOGIN_PATTERN =
            Pattern.compile("^[A-Za-z\\d](?:[A-Za-z\\d._@-]{0,126}[A-Za-z\\d])?$");
    private static final Pattern PROVIDER_ID_PATTERN = Pattern.compile("^[a-z\\d](?:[a-z\\d-]{0,48}[a-z\\d])?$");

    private final BootstrapSettingsProperties bootstrapSettingsProperties;
    private final SecuritySettingsProperties securitySettingsProperties;
    private final Environment environment;

    @Override
    public void afterPropertiesSet() {
        validateRemovedSettings();
        if (!environment.acceptsProfiles(Profiles.of("prod"))) {
            return;
        }
        validateSessionContract();
        validateInitialAdminIdentities();
        validateForwardedHeaderStrategy();
        validateOAuthSettings();
    }

    private void validateSessionContract() {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        if (!session.isCookieSecure()) {
            throw new IllegalStateException(
                    "Prod profile requires SESSION_COOKIE_SECURE=true so authenticated browser sessions stay secure.");
        }
        if (session.getTimeout().compareTo(Duration.ofMinutes(5)) < 0) {
            throw new IllegalStateException(
                    "Prod profile requires server.servlet.session.timeout to be at least 5 minutes.");
        }
        if (!session.isCookieHttpOnly()) {
            throw new IllegalStateException("Prod profile requires server.servlet.session.cookie.http-only=true.");
        }
        if (!"lax".equalsIgnoreCase(session.getCookieSameSite())
                && !"strict".equalsIgnoreCase(session.getCookieSameSite())) {
            throw new IllegalStateException(
                    "Prod profile requires server.servlet.session.cookie.same-site to be lax or strict.");
        }
    }

    private void validateRemovedSettings() {
        String deprecatedDefaultProvider = firstNonBlank(
                environment.getProperty("OAUTH_DEFAULT_PROVIDER"),
                environment.getProperty("app.security.oauth.default-provider"));
        if (deprecatedDefaultProvider != null) {
            throw new IllegalStateException(
                    "OAUTH_DEFAULT_PROVIDER has been removed. Expose provider choices through GET /api/session"
                            + " loginProviders[].");
        }
        String deprecatedAdminLogins = firstNonBlank(
                environment.getProperty("ADMIN_LOGINS"), environment.getProperty("app.security.admin-logins"));
        if (deprecatedAdminLogins != null) {
            throw new IllegalStateException(
                    "ADMIN_LOGINS has been removed. Use APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES only for zero-admin"
                            + " bootstrap, then manage roles through /api/admin/users.");
        }
    }

    private void validateForwardedHeaderStrategy() {
        String forwardHeadersStrategy = environment.getProperty("server.forward-headers-strategy", "");
        if (!"framework".equalsIgnoreCase(forwardHeadersStrategy)) {
            throw new IllegalStateException("Prod profile requires server.forward-headers-strategy=framework.");
        }
    }

    private void validateInitialAdminIdentities() {
        for (String identity : bootstrapSettingsProperties.normalizedInitialAdminIdentities()) {
            int separatorIndex = identity.indexOf(':');
            if (separatorIndex <= 0 || separatorIndex == identity.length() - 1) {
                throw invalidInitialAdminIdentities(identity);
            }

            String providerId = identity.substring(0, separatorIndex);
            String externalLogin = identity.substring(separatorIndex + 1);
            if (!PROVIDER_ID_PATTERN.matcher(providerId).matches()
                    || !EXTERNAL_LOGIN_PATTERN.matcher(externalLogin).matches()) {
                throw invalidInitialAdminIdentities(identity);
            }
        }
    }

    private void validateOAuthSettings() {
        if (!environment.acceptsProfiles(Profiles.of("oauth"))) {
            return;
        }
        SecuritySettingsProperties.OAuth oauthSettings = securitySettingsProperties.getOAuth();
        Map<String, SecuritySettingsProperties.OAuth.Provider> configuredProviders =
                oauthSettings.configuredProviders();
        if (configuredProviders.isEmpty()) {
            throw new IllegalStateException(
                    "OAuth-enabled prod profile requires at least one configured identity provider.");
        }

        for (Map.Entry<String, SecuritySettingsProperties.OAuth.Provider> providerEntry :
                configuredProviders.entrySet()) {
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
                            + registrationId);
        }
    }

    private void validateProviderConfiguration(
            String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        if (provider.getType() == null) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires a type of GITHUB or OIDC.".formatted(registrationId));
        }
        if (!provider.hasClientCredentials()) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires both client-id and client-secret.".formatted(registrationId));
        }
        switch (provider.getType()) {
            case GITHUB -> validateGithubProvider(registrationId, provider);
            case OIDC -> validateOidcProvider(registrationId, provider);
        }
    }

    private void validateGithubProvider(String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        if (!provider.normalizedIssuerUri().isBlank()) {
            throw new IllegalStateException(
                    "GitHub provider '%s' must not define issuer-uri.".formatted(registrationId));
        }
    }

    private void validateOidcProvider(String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        if (provider.normalizedIssuerUri().isBlank()) {
            throw new IllegalStateException("OIDC provider '%s' requires issuer-uri.".formatted(registrationId));
        }
        if (!provider.normalizedScope().contains("openid")) {
            throw new IllegalStateException("OIDC provider '%s' scope must include openid.".formatted(registrationId));
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

    private IllegalStateException invalidInitialAdminIdentities(String identity) {
        return new IllegalStateException(
                "APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES must contain comma-separated provider:externalLogin values."
                        + " Invalid value: "
                        + identity);
    }
}
