package team.jit.technicalinterviewdemo.technical.security;

import jakarta.validation.constraints.Min;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Locale;
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

        private final Github github = new Github();

        public Github getGithub() {
            return github;
        }

        public static class Github {

            private String clientId = "";
            private String clientSecret = "";

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
        }
    }
}
