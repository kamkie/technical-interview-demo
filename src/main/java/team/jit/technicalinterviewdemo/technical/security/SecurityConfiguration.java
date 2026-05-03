package team.jit.technicalinterviewdemo.technical.security;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
public class SecurityConfiguration {

    @Bean
    AuthenticatedUserSynchronizationFilter authenticatedUserSynchronizationFilter(
            CurrentUserAccountService currentUserAccountService
    ) {
        return new AuthenticatedUserSynchronizationFilter(currentUserAccountService);
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
            AuthenticatedUserSynchronizationFilter authenticatedUserSynchronizationFilter,
            ApiAuthenticationEntryPoint apiAuthenticationEntryPoint,
            ApiAccessDeniedHandler apiAccessDeniedHandler,
            SessionRegistry sessionRegistry,
            SecuritySettingsProperties securitySettingsProperties,
            Environment environment
    ) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 1.0 keeps CSRF disabled to preserve reviewer-friendly session flows in the demo.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/", "/docs", "/docs/**", "/hello").permitAll()
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/api/account", "/api/account/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/audit-logs").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/operator/surface").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        // Prometheus stays reachable for trusted deployment scraping; deployment boundaries keep it off the internet.
                        .requestMatchers(HttpMethod.GET, "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books", "/api/categories", "/api/localizations")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/books/*", "/api/categories/*", "/api/localizations/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*", "/api/categories/*", "/api/localizations/*").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                apiAuthenticationEntryPoint,
                                request -> request.getRequestURI().startsWith("/api/")
                        )
                        .defaultAccessDeniedHandlerFor(
                                apiAccessDeniedHandler,
                                request -> request.getRequestURI().startsWith("/api/")
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                )
                .addFilterAfter(authenticatedUserSynchronizationFilter, AuthorizationFilter.class);

        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            http.sessionManagement(session -> session
                    .sessionConcurrency(concurrency -> concurrency
                            .maximumSessions(securitySettingsProperties.getSession().getMaxConcurrentSessions())
                            .maxSessionsPreventsLogin(securitySettingsProperties.getSession().isMaxSessionsPreventsLogin())
                            .sessionRegistry(sessionRegistry)
                    )
            );
        }

        ClientRegistrationRepository registrationRepository = clientRegistrationRepository.getIfAvailable();
        if (registrationRepository != null) {
            Optional<String> loginPage = resolvedLoginPage(securitySettingsProperties, registrationRepository);
            if (loginPage.isPresent()) {
                http.oauth2Login(oauth2 -> oauth2.loginPage(loginPage.get()));
            } else {
                http.oauth2Login(Customizer.withDefaults());
            }
        }

        return http.build();
    }

    private Optional<String> resolvedLoginPage(
            SecuritySettingsProperties securitySettingsProperties,
            ClientRegistrationRepository registrationRepository
    ) {
        Set<String> registrationIds = registrationIds(registrationRepository);
        Optional<String> configuredLoginProvider = securitySettingsProperties.getOAuth()
                .resolvedLoginProvider()
                .filter(registrationIds::contains);
        if (configuredLoginProvider.isPresent()) {
            return configuredLoginProvider.map(SecuritySettingsProperties.OAuth::authorizationPath);
        }
        if (registrationIds.size() == 1) {
            return registrationIds.stream()
                    .findFirst()
                    .map(SecuritySettingsProperties.OAuth::authorizationPath);
        }
        return Optional.empty();
    }

    private Set<String> registrationIds(ClientRegistrationRepository registrationRepository) {
        Set<String> registrationIds = new LinkedHashSet<>();
        if (registrationRepository instanceof Iterable<?> iterableRegistrationRepository) {
            for (Object registration : iterableRegistrationRepository) {
                if (registration instanceof ClientRegistration clientRegistration) {
                    registrationIds.add(clientRegistration.getRegistrationId());
                }
            }
        }
        return registrationIds;
    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
        return new SpringSessionBackedSessionRegistry((FindByIndexNameSessionRepository) sessionRepository);
    }
}
