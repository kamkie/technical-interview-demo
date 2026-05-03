package team.jit.technicalinterviewdemo.technical.security;

import jakarta.validation.constraints.Min;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecuritySettingsProperties {

    private Set<String> adminLogins = new LinkedHashSet<>();
    private final Session session = new Session();
    private final OAuth oauth = new OAuth();

    public Set<String> normalizedAdminLogins() {
        return adminLogins.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> getAdminLogins() {
        return adminLogins;
    }

    public void setAdminLogins(Set<String> adminLogins) {
        this.adminLogins = adminLogins;
    }

    public Session getSession() {
        return session;
    }

    public OAuth getOAuth() {
        return oauth;
    }

    public static class Session {

        private Duration timeout = Duration.ofMinutes(30);
        private boolean cookieSecure;
        private boolean cookieHttpOnly = true;
        private String cookieSameSite = "lax";

        @Min(1)
        private int maxConcurrentSessions = 1;

        private boolean maxSessionsPreventsLogin = true;

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public boolean isCookieSecure() {
            return cookieSecure;
        }

        public void setCookieSecure(boolean cookieSecure) {
            this.cookieSecure = cookieSecure;
        }

        public boolean isCookieHttpOnly() {
            return cookieHttpOnly;
        }

        public void setCookieHttpOnly(boolean cookieHttpOnly) {
            this.cookieHttpOnly = cookieHttpOnly;
        }

        public String getCookieSameSite() {
            return cookieSameSite;
        }

        public void setCookieSameSite(String cookieSameSite) {
            this.cookieSameSite = cookieSameSite;
        }

        public int getMaxConcurrentSessions() {
            return maxConcurrentSessions;
        }

        public void setMaxConcurrentSessions(int maxConcurrentSessions) {
            this.maxConcurrentSessions = maxConcurrentSessions;
        }

        public boolean isMaxSessionsPreventsLogin() {
            return maxSessionsPreventsLogin;
        }

        public void setMaxSessionsPreventsLogin(boolean maxSessionsPreventsLogin) {
            this.maxSessionsPreventsLogin = maxSessionsPreventsLogin;
        }
    }

    public static class OAuth {

        private String defaultProvider = "";
        private Map<String, Provider> providers = new LinkedHashMap<>();

        public String getDefaultProvider() {
            return defaultProvider;
        }

        public void setDefaultProvider(String defaultProvider) {
            this.defaultProvider = defaultProvider;
        }

        public Map<String, Provider> getProviders() {
            return providers;
        }

        public void setProviders(Map<String, Provider> providers) {
            this.providers = providers;
        }

        public Map<String, Provider> configuredProviders() {
            return providers.entrySet().stream()
                    .map(entry -> Map.entry(normalizeRegistrationId(entry.getKey()), entry.getValue()))
                    .filter(entry -> !entry.getKey().isBlank())
                    .filter(entry -> entry.getValue() != null && entry.getValue().hasCredentialMaterial())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (first, ignored) -> first,
                            LinkedHashMap::new
                    ));
        }

        public Optional<String> normalizedDefaultProvider() {
            String normalized = normalizeRegistrationId(defaultProvider);
            if (normalized.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(normalized);
        }

        public Optional<String> resolvedLoginProvider() {
            Map<String, Provider> configuredProviders = configuredProviders();
            Optional<String> defaultProviderCandidate = normalizedDefaultProvider();
            if (defaultProviderCandidate.isPresent()
                    && (configuredProviders.isEmpty() || configuredProviders.containsKey(defaultProviderCandidate.get()))) {
                return defaultProviderCandidate;
            }
            if (configuredProviders.size() == 1) {
                return configuredProviders.keySet().stream().findFirst();
            }
            return Optional.empty();
        }

        public Optional<String> resolvedLoginPath() {
            return resolvedLoginProvider().map(OAuth::authorizationPath);
        }

        public static String authorizationPath(String providerId) {
            return "/oauth2/authorization/" + normalizeRegistrationId(providerId);
        }

        private static String normalizeRegistrationId(String providerId) {
            if (providerId == null) {
                return "";
            }
            return providerId.trim().toLowerCase(Locale.ROOT);
        }

        public static class Provider {

            private ProviderType type;
            private String clientId = "";
            private String clientSecret = "";
            private String issuerUri = "";
            private String userNameAttribute = "";
            private Set<String> scope = new LinkedHashSet<>();

            public ProviderType getType() {
                return type;
            }

            public void setType(ProviderType type) {
                this.type = type;
            }

            public String getClientId() {
                return clientId;
            }

            public void setClientId(String clientId) {
                this.clientId = clientId;
            }

            public String getClientSecret() {
                return clientSecret;
            }

            public void setClientSecret(String clientSecret) {
                this.clientSecret = clientSecret;
            }

            public String getIssuerUri() {
                return issuerUri;
            }

            public void setIssuerUri(String issuerUri) {
                this.issuerUri = issuerUri;
            }

            public String getUserNameAttribute() {
                return userNameAttribute;
            }

            public void setUserNameAttribute(String userNameAttribute) {
                this.userNameAttribute = userNameAttribute;
            }

            public Set<String> getScope() {
                return scope;
            }

            public void setScope(Set<String> scope) {
                this.scope = scope;
            }

            public boolean hasCredentialMaterial() {
                return !normalizedClientId().isBlank()
                        || !normalizedClientSecret().isBlank()
                        || !normalizedIssuerUri().isBlank();
            }

            public boolean hasClientCredentials() {
                return !normalizedClientId().isBlank() && !normalizedClientSecret().isBlank();
            }

            public String normalizedClientId() {
                return normalizeString(clientId);
            }

            public String normalizedClientSecret() {
                return normalizeString(clientSecret);
            }

            public String normalizedIssuerUri() {
                return normalizeString(issuerUri);
            }

            public String normalizedUserNameAttribute() {
                return normalizeString(userNameAttribute);
            }

            public Set<String> normalizedScope() {
                return scope.stream()
                        .map(Provider::normalizeString)
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            }

            private static String normalizeString(String value) {
                if (value == null) {
                    return "";
                }
                return value.trim();
            }
        }

        public enum ProviderType {
            GITHUB,
            OIDC
        }
    }
}
