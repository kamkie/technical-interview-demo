package team.jit.technicalinterviewdemo.technical;

import org.junit.jupiter.api.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import team.jit.technicalinterviewdemo.TechnicalInterviewDemoApplication;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;
import team.jit.technicalinterviewdemo.technical.security.SecuritySettingsProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductionConfigurationTests {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16-alpine");

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("technical_interview_demo")
            .withUsername("postgres")
            .withPassword("changeme");

    @BeforeAll
    static void startPostgres() {
        POSTGRESQL_CONTAINER.start();
    }

    @AfterAll
    static void stopPostgres() {
        POSTGRESQL_CONTAINER.stop();
    }

    private static final Map<String, String> EXPECTED_FAILURES = Map.of(
            "DATABASE_HOST", "${DATABASE_HOST}",
            "DATABASE_PORT", "${DATABASE_PORT}",
            "DATABASE_NAME", "${DATABASE_NAME}",
            "DATABASE_USER", "${DATABASE_USER}",
            "DATABASE_PASSWORD", "password authentication failed"
    );

    @TestFactory
    Stream<DynamicTest> prodProfileFailsFastWhenRequiredDatabaseVariablesAreMissing() {
        return EXPECTED_FAILURES.keySet().stream()
                .sorted()
                .map(variableName -> DynamicTest.dynamicTest(
                        variableName,
                        () -> assertThatThrownBy(() -> runProdApplicationWithout(variableName))
                                .hasStackTraceContaining(EXPECTED_FAILURES.get(variableName))
                ));
    }

    @Test
    void prodProfileExposesHardenedSessionSettings() {
        try (ConfigurableApplicationContext context = runProdApplication()) {
            SecuritySettingsProperties securitySettingsProperties = context.getBean(SecuritySettingsProperties.class);
            BootstrapSettingsProperties bootstrapSettingsProperties = context.getBean(BootstrapSettingsProperties.class);

            assertThat(context.getEnvironment().getProperty("server.servlet.session.timeout")).isEqualTo("15m");
            assertThat(context.getEnvironment().getProperty("server.forward-headers-strategy")).isEqualTo("framework");
            assertThat(securitySettingsProperties.getSession().isCookieSecure()).isTrue();
            assertThat(securitySettingsProperties.getSession().getMaxConcurrentSessions()).isEqualTo(1);
            assertThat(securitySettingsProperties.getSession().isMaxSessionsPreventsLogin()).isTrue();
            assertThat(bootstrapSettingsProperties.getSeed().isDemoData()).isFalse();
        }
    }

    @Test
    void prodProfileFailsFastWhenSecureSessionCookieIsDisabled() {
        assertThatThrownBy(() -> runProdApplication("--SESSION_COOKIE_SECURE=false"))
                .hasStackTraceContaining("SESSION_COOKIE_SECURE=true");
    }

    @Test
    void prodProfileFailsFastWhenDeprecatedAdminLoginsSettingIsPresent() {
        assertThatThrownBy(() -> runProdApplication("--ADMIN_LOGINS=admin-user"))
                .hasStackTraceContaining("ADMIN_LOGINS has been removed");
    }

    @Test
    void prodProfileFailsFastWhenInitialAdminIdentitiesContainInvalidValue() {
        assertThatThrownBy(() -> runProdApplication("--APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES=invalid login"))
                .hasStackTraceContaining(
                        "APP_BOOTSTRAP_INITIAL_ADMIN_IDENTITIES must contain comma-separated provider:externalLogin values"
                );
    }

    @Test
    void prodProfileFailsFastWhenForwardedHeaderStrategyIsNotFramework() {
        assertThatThrownBy(() -> runProdApplication("--server.forward-headers-strategy=native"))
                .hasStackTraceContaining("server.forward-headers-strategy=framework");
    }

    @Test
    void prodProfileWithOauthFailsFastWhenProviderClientIdIsMissing() {
        assertThatThrownBy(() -> runProdApplication(
                "--spring.profiles.active=prod,oauth",
                "--GITHUB_CLIENT_ID=",
                "--GITHUB_CLIENT_SECRET=demo-secret"
        ))
                .hasStackTraceContaining("OAuth provider 'github' requires both client-id and client-secret.");
    }

    @Test
    void prodProfileWithOauthFailsFastWhenProviderClientSecretIsMissing() {
        assertThatThrownBy(() -> runProdApplication(
                "--spring.profiles.active=prod,oauth",
                "--GITHUB_CLIENT_ID=demo-client",
                "--GITHUB_CLIENT_SECRET="
        )).hasStackTraceContaining("OAuth provider 'github' requires both client-id and client-secret.");
    }

    @Test
    void prodProfileWithOauthFailsFastWhenOidcProviderIssuerIsMissing() {
        assertThatThrownBy(() -> runProdApplication(
                "--spring.profiles.active=prod,oauth",
                "--GITHUB_CLIENT_ID=",
                "--GITHUB_CLIENT_SECRET=",
                "--OIDC_CLIENT_ID=oidc-client",
                "--OIDC_CLIENT_SECRET=oidc-secret",
                "--OIDC_ISSUER_URI="
        )).hasStackTraceContaining("OIDC provider 'oidc' requires issuer-uri.");
    }

    @Test
    void prodProfileWithOauthAllowsMultipleProvidersWithoutDefaultProvider() {
        try (ConfigurableApplicationContext context = runProdApplication(
                "--spring.profiles.active=prod,oauth",
                "--GITHUB_CLIENT_ID=github-client",
                "--GITHUB_CLIENT_SECRET=github-secret",
                "--app.security.oauth.providers.internal.type=GITHUB",
                "--app.security.oauth.providers.internal.client-id=internal-client",
                "--app.security.oauth.providers.internal.client-secret=internal-secret"
        )) {
            ClientRegistrationRepository clientRegistrationRepository = context.getBean(ClientRegistrationRepository.class);
            assertThat(clientRegistrationRepository.findByRegistrationId("github")).isNotNull();
            assertThat(clientRegistrationRepository.findByRegistrationId("internal")).isNotNull();
        }
    }

    @Test
    void prodProfileWithOauthFailsFastWhenDeprecatedDefaultProviderSettingIsPresent() {
        assertThatThrownBy(() -> runProdApplication(
                "--spring.profiles.active=prod,oauth",
                "--OAUTH_DEFAULT_PROVIDER=github",
                "--GITHUB_CLIENT_ID=demo-client",
                "--GITHUB_CLIENT_SECRET=demo-secret"
        )).hasStackTraceContaining("OAUTH_DEFAULT_PROVIDER has been removed");
    }

    private static void runProdApplicationWithout(String missingVariableName) {
        try (ConfigurableApplicationContext ignored = runApplication(argumentsWithout(missingVariableName))) {
            // The prod profile should fail before the context starts when a required variable is missing.
        }
    }

    private static ConfigurableApplicationContext runProdApplication(String... overrides) {
        return runApplication(argumentsWithDefaults(overrides));
    }

    private static String[] argumentsWithout(String missingVariableName) {
        Map<String, String> requiredDatabaseProperties = requiredDatabaseProperties();
        List<String> arguments = new ArrayList<>(List.of(
                "--spring.profiles.active=prod",
                "--spring.main.banner-mode=off",
                "--logging.level.root=OFF"
        ));
        for (Map.Entry<String, String> entry : requiredDatabaseProperties.entrySet()) {
            if (!entry.getKey().equals(missingVariableName)) {
                arguments.add("--" + entry.getKey() + "=" + entry.getValue());
            }
        }
        return arguments.toArray(String[]::new);
    }

    private static String[] argumentsWithDefaults(String... overrides) {
        Map<String, String> argumentsByKey = new LinkedHashMap<>();
        argumentsByKey.put("spring.profiles.active", "prod");
        argumentsByKey.put("spring.main.banner-mode", "off");
        argumentsByKey.put("logging.level.root", "OFF");
        argumentsByKey.put("server.port", "0");
        argumentsByKey.put("SESSION_COOKIE_SECURE", "true");
        requiredDatabaseProperties().forEach(argumentsByKey::put);
        for (String override : overrides) {
            String withoutPrefix = override.substring(2);
            int separatorIndex = withoutPrefix.indexOf('=');
            argumentsByKey.put(
                    withoutPrefix.substring(0, separatorIndex),
                    withoutPrefix.substring(separatorIndex + 1)
            );
        }
        return argumentsByKey.entrySet().stream()
                .map(entry -> "--" + entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
    }

    private static Map<String, String> requiredDatabaseProperties() {
        return Map.of(
                "DATABASE_HOST", POSTGRESQL_CONTAINER.getHost(),
                "DATABASE_PORT", String.valueOf(POSTGRESQL_CONTAINER.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)),
                "DATABASE_NAME", POSTGRESQL_CONTAINER.getDatabaseName(),
                "DATABASE_USER", POSTGRESQL_CONTAINER.getUsername(),
                "DATABASE_PASSWORD", POSTGRESQL_CONTAINER.getPassword()
        );
    }

    private static ConfigurableApplicationContext runApplication(String[] arguments) {
        return new SpringApplicationBuilder(TechnicalInterviewDemoApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(arguments);
    }
}
