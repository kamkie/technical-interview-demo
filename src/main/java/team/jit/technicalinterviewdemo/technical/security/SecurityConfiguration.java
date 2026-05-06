package team.jit.technicalinterviewdemo.technical.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;

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
                                            HttpSecurity http, ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository, AuthenticatedUserSynchronizationFilter authenticatedUserSynchronizationFilter, ApiAuthenticationEntryPoint apiAuthenticationEntryPoint, ApiAccessDeniedHandler apiAccessDeniedHandler, SessionRegistry sessionRegistry, SecuritySettingsProperties securitySettingsProperties, Environment environment, CurrentApplicationSessionResolver currentApplicationSessionResolver, CsrfTokenRepository csrfTokenRepository, CsrfTokenRequestHandler csrfTokenRequestHandler, AuthenticationSuccessHandler oauthAuthenticationSuccessHandler, AuthenticationFailureHandler oauthAuthenticationFailureHandler
    ) throws Exception {
        boolean prodProfileActive = environment.acceptsProfiles(Profiles.of("prod"));

        http.formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable).csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository).csrfTokenRequestHandler(csrfTokenRequestHandler).requireCsrfProtectionMatcher(new CurrentSessionCsrfProtectionMatcher(currentApplicationSessionResolver))
        ).headers(headers -> configureSecurityHeaders(headers, prodProfileActive)).authorizeHttpRequests(authorize -> authorize.requestMatchers("/error", "/", "/docs", "/docs/**", "/hello").permitAll().requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll().requestMatchers(SecuritySettingsProperties.OAuth.AUTHORIZATION_BASE_URI + "/**").permitAll().requestMatchers(SecuritySettingsProperties.OAuth.CALLBACK_BASE_URI + "/**").permitAll().requestMatchers(HttpMethod.GET, "/api/session").permitAll().requestMatchers(HttpMethod.POST, "/api/session/logout").permitAll().requestMatchers("/api/admin/**").authenticated().requestMatchers("/api/account", "/api/account/**").authenticated().requestMatchers(HttpMethod.GET, "/api/**").permitAll().requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                // Prometheus stays reachable for trusted deployment scraping; deployment boundaries keep it off the internet.
                .requestMatchers(HttpMethod.GET, "/actuator/info", "/actuator/prometheus").permitAll().requestMatchers(HttpMethod.POST, "/api/books", "/api/categories", "/api/localizations").authenticated().requestMatchers(HttpMethod.PUT, "/api/books/*", "/api/categories/*", "/api/localizations/*").authenticated().requestMatchers(HttpMethod.DELETE, "/api/books/*", "/api/categories/*", "/api/localizations/*").authenticated().anyRequest().permitAll()
        ).exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                apiAuthenticationEntryPoint, request -> request.getRequestURI().startsWith("/api/")
        ).defaultAccessDeniedHandlerFor(
                apiAccessDeniedHandler, request -> request.getRequestURI().startsWith("/api/")
        )
        ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation(sessionFixation -> sessionFixation.migrateSession())
        ).addFilterAfter(authenticatedUserSynchronizationFilter, AuthorizationFilter.class);

        if (prodProfileActive) {
            http.sessionManagement(session -> session.sessionConcurrency(concurrency -> concurrency.maximumSessions(securitySettingsProperties.getSession().getMaxConcurrentSessions()).maxSessionsPreventsLogin(securitySettingsProperties.getSession().isMaxSessionsPreventsLogin()).sessionRegistry(sessionRegistry)
            )
            );
        }

        ClientRegistrationRepository registrationRepository = clientRegistrationRepository.getIfAvailable();
        if (registrationRepository != null) {
            http.oauth2Login(oauth2 -> oauth2.loginPage("/api/session").authorizationEndpoint(authorization -> authorization.baseUri(SecuritySettingsProperties.OAuth.AUTHORIZATION_BASE_URI)
            ).redirectionEndpoint(redirection -> redirection.baseUri(SecuritySettingsProperties.OAuth.REDIRECTION_ENDPOINT_BASE_URI)
            ).successHandler(oauthAuthenticationSuccessHandler).failureHandler(oauthAuthenticationFailureHandler)
            );
        }

        return http.build();
    }

    @Bean
    CsrfTokenRepository csrfTokenRepository(SecuritySettingsProperties securitySettingsProperties) {
        SecuritySettingsProperties.Session session = securitySettingsProperties.getSession();
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName(SameSiteCsrfContract.COOKIE_NAME);
        repository.setHeaderName(SameSiteCsrfContract.HEADER_NAME);
        repository.setCookiePath("/");
        repository.setCookieCustomizer(cookie -> {
            cookie.secure(session.isCookieSecure());
            if (session.getCookieSameSite() != null && !session.getCookieSameSite().isBlank()) {
                cookie.sameSite(session.getCookieSameSite());
            }
        });
        return repository;
    }

    @Bean
    CsrfTokenRequestHandler csrfTokenRequestHandler() {
        return new SpaCsrfTokenRequestHandler();
    }

    private void configureSecurityHeaders(HeadersConfigurer<HttpSecurity> headers, boolean prodProfileActive) {
        headers.contentTypeOptions(Customizer.withDefaults());
        headers.frameOptions(frameOptions -> frameOptions.deny());
        headers.referrerPolicy(referrerPolicy -> referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
        );
        headers.permissionsPolicyHeader(permissionsPolicy -> permissionsPolicy.policy("geolocation=(), microphone=(), camera=()")
        );
        if (prodProfileActive) {
            headers.httpStrictTransportSecurity(Customizer.withDefaults());
        } else {
            headers.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable);
        }
    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<? extends Session> sessionRepository) {
        return new SpringSessionBackedSessionRegistry((FindByIndexNameSessionRepository) sessionRepository);
    }

    @Bean
    AuthenticationSuccessHandler oauthAuthenticationSuccessHandler(
                                                                   CurrentUserAccountService currentUserAccountService, AuditLogService auditLogService
    ) {
        return new AuditingAuthenticationSuccessHandler(currentUserAccountService, auditLogService);
    }

    @Bean
    AuthenticationFailureHandler oauthAuthenticationFailureHandler(AuditLogService auditLogService) {
        return new AuditingAuthenticationFailureHandler(auditLogService);
    }
}
