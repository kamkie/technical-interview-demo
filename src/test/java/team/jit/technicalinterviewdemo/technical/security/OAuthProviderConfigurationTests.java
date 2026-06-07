package team.jit.technicalinterviewdemo.technical.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuthProviderConfigurationTests {

    @Test
    void oauthAuthorizationPathUsesApiSessionBaseUri() {
        assertThat(SecuritySettingsProperties.OAuth.authorizationPath("Internal"))
                .isEqualTo("/api/session/oauth2/authorization/internal");
    }

    @Test
    void oauthClientRegistrationRepositoryBuildsConfiguredGithubProvider() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of(
                        "github",
                        provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret"))));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();
        ClientRegistrationRepository repository = configuration.clientRegistrationRepository(settings);

        assertThat(repository.findByRegistrationId("github")).isNotNull();
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsMissingClientSecret() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of(
                        "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", ""))));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires both client-id and client-secret");
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsMissingProviderType() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("custom", provider(null, "client", "secret"))));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires a provider type");
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsOidcWithoutIssuer() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of(
                        "oidc",
                        provider(SecuritySettingsProperties.OAuth.ProviderType.OIDC, "oidc-client", "oidc-secret"))));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires issuer-uri");
    }

    @Test
    void oauthClientRegistrationRepositoryBuildsConfiguredFakeProvider() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of("smoke", fakeProvider("smoke-client", "smoke-secret"))));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();
        ClientRegistrationRepository repository = configuration.clientRegistrationRepository(settings);

        assertThat(repository.findByRegistrationId("smoke")).isNotNull();
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsFakeProviderWithoutTokenUri() {
        SecuritySettingsProperties settings = baseSettings();
        SecuritySettingsProperties.OAuth.Provider provider = fakeProvider("smoke-client", "smoke-secret");
        provider.setTokenUri("");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("smoke", provider)));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires token-uri");
    }

    @Test
    void productionValidatorAllowsProdWithoutOauthProfile() {
        SecuritySettingsProperties settings = baseSettings();
        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        environment.setActiveProfiles("prod");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatCode(validator::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void productionValidatorRejectsInvalidProviderId() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of(
                        "invalid_provider",
                        provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "client", "secret"))));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OAuth provider id must match");
    }

    @Test
    void productionValidatorRejectsGithubProviderWithIssuer() {
        SecuritySettingsProperties settings = baseSettings();
        SecuritySettingsProperties.OAuth.Provider provider =
                provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret");
        provider.setIssuerUri("https://issuer.example.com");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("github", provider)));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not define issuer-uri");
    }

    @Test
    void productionValidatorRejectsOidcWithoutOpenidScope() {
        SecuritySettingsProperties settings = baseSettings();
        SecuritySettingsProperties.OAuth.Provider provider =
                provider(SecuritySettingsProperties.OAuth.ProviderType.OIDC, "oidc-client", "oidc-secret");
        provider.setIssuerUri("https://issuer.example.com");
        provider.setScope(java.util.Set.of("profile", "email"));
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("oidc", provider)));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scope must include openid");
    }

    @Test
    void productionValidatorRejectsFakeOauthProfile() {
        SecuritySettingsProperties settings = baseSettings();
        MockEnvironment environment =
                new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth,fake-oauth");
        environment.setActiveProfiles("prod", "oauth", "fake-oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not run with the fake-oauth");
    }

    @Test
    void productionValidatorRejectsFakeProviderType() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of("smoke", fakeProvider("smoke-client", "smoke-secret"))));
        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not be configured with the prod profile");
    }

    @Test
    void productionValidatorAllowsMultipleProvidersWithoutDefaultProvider() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth()
                .setProviders(new LinkedHashMap<>(Map.of(
                        "github",
                        provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret"),
                        "internal",
                        provider(
                                SecuritySettingsProperties.OAuth.ProviderType.GITHUB,
                                "internal-client",
                                "internal-secret"))));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatCode(validator::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void productionValidatorRejectsDeprecatedDefaultProviderSetting() {
        SecuritySettingsProperties settings = baseSettings();
        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");
        environment.setProperty("OAUTH_DEFAULT_PROVIDER", "github");
        environment.setProperty("server.forward-headers-strategy", "framework");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OAUTH_DEFAULT_PROVIDER has been removed");
    }

    @Test
    void productionValidatorRejectsNonFrameworkForwardedHeaderStrategy() {
        SecuritySettingsProperties settings = baseSettings();
        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        environment.setActiveProfiles("prod");
        environment.setProperty("server.forward-headers-strategy", "native");

        ProductionSecurityConfigurationValidator validator = validator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("server.forward-headers-strategy=framework");
    }

    private SecuritySettingsProperties baseSettings() {
        SecuritySettingsProperties settings = new SecuritySettingsProperties();
        settings.getSession().setCookieSecure(true);
        return settings;
    }

    private ProductionSecurityConfigurationValidator validator(
            SecuritySettingsProperties settings, MockEnvironment environment) {
        return new ProductionSecurityConfigurationValidator(new BootstrapSettingsProperties(), settings, environment);
    }

    private SecuritySettingsProperties.OAuth.Provider provider(
            SecuritySettingsProperties.OAuth.ProviderType type, String clientId, String clientSecret) {
        SecuritySettingsProperties.OAuth.Provider provider = new SecuritySettingsProperties.OAuth.Provider();
        provider.setType(type);
        provider.setClientId(clientId);
        provider.setClientSecret(clientSecret);
        return provider;
    }

    private SecuritySettingsProperties.OAuth.Provider fakeProvider(String clientId, String clientSecret) {
        SecuritySettingsProperties.OAuth.Provider provider =
                provider(SecuritySettingsProperties.OAuth.ProviderType.FAKE, clientId, clientSecret);
        provider.setClientName("Smoke OAuth");
        provider.setAuthorizationUri("/test-support/oauth2/authorize");
        provider.setTokenUri("http://127.0.0.1:8080/test-support/oauth2/token");
        provider.setUserInfoUri("http://127.0.0.1:8080/test-support/oauth2/userinfo");
        provider.setUserNameAttribute("login");
        return provider;
    }
}
