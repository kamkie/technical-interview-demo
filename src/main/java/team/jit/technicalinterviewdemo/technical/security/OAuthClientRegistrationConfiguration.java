package team.jit.technicalinterviewdemo.technical.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@Profile("oauth")
public class OAuthClientRegistrationConfiguration {

    @Bean
    ClientRegistrationRepository clientRegistrationRepository(SecuritySettingsProperties securitySettingsProperties) {
        Map<String, SecuritySettingsProperties.OAuth.Provider> configuredProviders =
                securitySettingsProperties.getOAuth().configuredProviders();

        if (configuredProviders.isEmpty()) {
            throw new IllegalStateException(
                    "OAuth profile requires at least one configured identity provider with client credentials.");
        }

        List<ClientRegistration> registrations = new ArrayList<>();
        for (Map.Entry<String, SecuritySettingsProperties.OAuth.Provider> configuredProvider :
                configuredProviders.entrySet()) {
            String registrationId = configuredProvider.getKey();
            SecuritySettingsProperties.OAuth.Provider provider = configuredProvider.getValue();
            registrations.add(clientRegistration(registrationId, provider));
        }
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration clientRegistration(
            String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        if (!provider.hasClientCredentials()) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires both client-id and client-secret.".formatted(registrationId));
        }

        SecuritySettingsProperties.OAuth.ProviderType providerType = provider.getType();
        if (providerType == null) {
            throw new IllegalStateException(
                    "OAuth provider '%s' requires a provider type (GITHUB or OIDC).".formatted(registrationId));
        }

        return switch (providerType) {
            case GITHUB -> githubRegistration(registrationId, provider);
            case OIDC -> oidcRegistration(registrationId, provider);
        };
    }

    private ClientRegistration githubRegistration(
            String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        ClientRegistration.Builder builder = CommonOAuth2Provider.GITHUB
                .getBuilder(registrationId)
                .clientId(provider.normalizedClientId())
                .clientSecret(provider.normalizedClientSecret())
                .redirectUri(SecuritySettingsProperties.OAuth.REDIRECT_URI_TEMPLATE);

        Set<String> scope = provider.normalizedScope();
        if (!scope.isEmpty()) {
            builder.scope(scope);
        }

        String userNameAttribute = provider.normalizedUserNameAttribute();
        if (!userNameAttribute.isBlank()) {
            builder.userNameAttributeName(userNameAttribute);
        }

        return builder.build();
    }

    private ClientRegistration oidcRegistration(
            String registrationId, SecuritySettingsProperties.OAuth.Provider provider) {
        String issuerUri = provider.normalizedIssuerUri();
        if (issuerUri.isBlank()) {
            throw new IllegalStateException("OIDC provider '%s' requires issuer-uri.".formatted(registrationId));
        }

        ClientRegistration.Builder builder = ClientRegistrations.fromIssuerLocation(issuerUri)
                .registrationId(registrationId)
                .clientId(provider.normalizedClientId())
                .clientSecret(provider.normalizedClientSecret())
                .redirectUri(SecuritySettingsProperties.OAuth.REDIRECT_URI_TEMPLATE);

        Set<String> scope = provider.normalizedScope();
        if (!scope.isEmpty()) {
            builder.scope(scope);
        }

        String userNameAttribute = provider.normalizedUserNameAttribute();
        if (!userNameAttribute.isBlank()) {
            builder.userNameAttributeName(userNameAttribute);
        }

        return builder.build();
    }
}
