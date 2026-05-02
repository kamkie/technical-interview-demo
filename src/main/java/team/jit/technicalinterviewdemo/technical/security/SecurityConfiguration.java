package team.jit.technicalinterviewdemo.technical.security;

import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

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
            ApiAccessDeniedHandler apiAccessDeniedHandler
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
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        // Prometheus stays reachable for trusted deployment scraping; deployment boundaries keep it off the internet.
                        .requestMatchers(HttpMethod.GET, "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/books", "/api/categories", "/api/localizations")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/books/*", "/api/localizations/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/books/*", "/api/localizations/*").authenticated()
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

        if (clientRegistrationRepository.getIfAvailable() != null) {
            http.oauth2Login(oauth2 -> oauth2.loginPage("/oauth2/authorization/github"));
        }

        return http.build();
    }
}
