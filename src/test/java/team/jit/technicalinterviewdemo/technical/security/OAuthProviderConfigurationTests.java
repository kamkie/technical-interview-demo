package team.jit.technicalinterviewdemo.technical.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

class OAuthProviderConfigurationTests {

    @Test
    void oauthSettingsResolveConfiguredDefaultProviderPath() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("internal");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret"),
                "internal", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "internal-client", "internal-secret")
        )));

        assertThat(settings.getOAuth().resolvedLoginProvider()).contains("internal");
        assertThat(settings.getOAuth().resolvedLoginPath()).contains("/oauth2/authorization/internal");
    }

    @Test
    void oauthSettingsFallbackToSingleConfiguredProviderWhenDefaultIsNotConfigured() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("github");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "internal", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "internal-client", "internal-secret")
        )));

        assertThat(settings.getOAuth().resolvedLoginProvider()).contains("internal");
    }

    @Test
    void oauthSettingsReturnNoShortcutWhenMultipleProvidersLackValidDefault() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("missing");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret"),
                "internal", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "internal-client", "internal-secret")
        )));

        assertThat(settings.getOAuth().resolvedLoginProvider()).isEmpty();
        assertThat(settings.getOAuth().resolvedLoginPath()).isEmpty();
    }

    @Test
    void oauthClientRegistrationRepositoryBuildsConfiguredGithubProvider() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret")
        )));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();
        ClientRegistrationRepository repository = configuration.clientRegistrationRepository(settings);

        assertThat(repository.findByRegistrationId("github")).isNotNull();
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsMissingClientSecret() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "")
        )));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires both client-id and client-secret");
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsMissingProviderType() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "custom", provider(null, "client", "secret")
        )));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires a provider type");
    }

    @Test
    void oauthClientRegistrationRepositoryRejectsOidcWithoutIssuer() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "oidc", provider(SecuritySettingsProperties.OAuth.ProviderType.OIDC, "oidc-client", "oidc-secret")
        )));

        OAuthClientRegistrationConfiguration configuration = new OAuthClientRegistrationConfiguration();

        assertThatThrownBy(() -> configuration.clientRegistrationRepository(settings))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires issuer-uri");
    }

    @Test
    void productionValidatorAllowsProdWithoutOauthProfile() {
        SecuritySettingsProperties settings = baseSettings();
        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        environment.setActiveProfiles("prod");

        ProductionSecurityConfigurationValidator validator =
                new ProductionSecurityConfigurationValidator(settings, environment);

        assertThatCode(validator::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void productionValidatorRejectsInvalidProviderId() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("invalid_provider");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "invalid_provider", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "client", "secret")
        )));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");

        ProductionSecurityConfigurationValidator validator =
                new ProductionSecurityConfigurationValidator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OAuth provider id must match");
    }

    @Test
    void productionValidatorRejectsGithubProviderWithIssuer() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("github");
        SecuritySettingsProperties.OAuth.Provider provider =
                provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret");
        provider.setIssuerUri("https://issuer.example.com");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("github", provider)));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");

        ProductionSecurityConfigurationValidator validator =
                new ProductionSecurityConfigurationValidator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not define issuer-uri");
    }

    @Test
    void productionValidatorRejectsOidcWithoutOpenidScope() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("oidc");
        SecuritySettingsProperties.OAuth.Provider provider =
                provider(SecuritySettingsProperties.OAuth.ProviderType.OIDC, "oidc-client", "oidc-secret");
        provider.setIssuerUri("https://issuer.example.com");
        provider.setScope(java.util.Set.of("profile", "email"));
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of("oidc", provider)));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");

        ProductionSecurityConfigurationValidator validator =
                new ProductionSecurityConfigurationValidator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("scope must include openid");
    }

    @Test
    void productionValidatorRequiresDefaultProviderWhenMultipleProvidersConfigured() {
        SecuritySettingsProperties settings = baseSettings();
        settings.getOAuth().setDefaultProvider("");
        settings.getOAuth().setProviders(new LinkedHashMap<>(Map.of(
                "github", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "gh-client", "gh-secret"),
                "internal", provider(SecuritySettingsProperties.OAuth.ProviderType.GITHUB, "internal-client", "internal-secret")
        )));

        MockEnvironment environment = new MockEnvironment().withProperty("spring.profiles.active", "prod,oauth");
        environment.setActiveProfiles("prod", "oauth");

        ProductionSecurityConfigurationValidator validator =
                new ProductionSecurityConfigurationValidator(settings, environment);

        assertThatThrownBy(validator::afterPropertiesSet)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires OAUTH_DEFAULT_PROVIDER");
    }

    private SecuritySettingsProperties baseSettings() {
        SecuritySettingsProperties settings = new SecuritySettingsProperties();
        settings.getSession().setCookieSecure(true);
        return settings;
    }

    private SecuritySettingsProperties.OAuth.Provider provider(
            SecuritySettingsProperties.OAuth.ProviderType type,
            String clientId,
            String clientSecret
    ) {
        SecuritySettingsProperties.OAuth.Provider provider = new SecuritySettingsProperties.OAuth.Provider();
        provider.setType(type);
        provider.setClientId(clientId);
        provider.setClientSecret(clientSecret);
        return provider;
    }
}
