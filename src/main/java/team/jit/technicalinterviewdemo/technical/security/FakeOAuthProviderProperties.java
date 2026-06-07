package team.jit.technicalinterviewdemo.technical.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Profile("fake-oauth & !prod")
@Validated
@ConfigurationProperties(prefix = "app.security.fake-oauth")
public class FakeOAuthProviderProperties {

    @NotBlank
    private String clientId = "technical-interview-demo-smoke";

    @NotBlank
    private String clientSecret = "technical-interview-demo-smoke-secret";

    @NotBlank
    private String login = "smoke-user";

    @NotBlank
    private String displayName = "Smoke Test User";

    @NotBlank
    private String email = "smoke-user@example.test";

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
