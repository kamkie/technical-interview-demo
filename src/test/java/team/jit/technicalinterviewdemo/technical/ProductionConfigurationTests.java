package team.jit.technicalinterviewdemo.technical;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import team.jit.technicalinterviewdemo.TechnicalInterviewDemoApplication;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

class ProductionConfigurationTests {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:15-alpine");

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

    private static void runProdApplicationWithout(String missingVariableName) {
        try (ConfigurableApplicationContext ignored = new SpringApplicationBuilder(TechnicalInterviewDemoApplication.class)
                .web(WebApplicationType.NONE)
                .run(argumentsWithout(missingVariableName))) {
            // The prod profile should fail before the context starts when a required variable is missing.
        }
    }

    private static String[] argumentsWithout(String missingVariableName) {
        Map<String, String> requiredDatabaseProperties = Map.of(
                "DATABASE_HOST", POSTGRESQL_CONTAINER.getHost(),
                "DATABASE_PORT", String.valueOf(POSTGRESQL_CONTAINER.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)),
                "DATABASE_NAME", POSTGRESQL_CONTAINER.getDatabaseName(),
                "DATABASE_USER", POSTGRESQL_CONTAINER.getUsername(),
                "DATABASE_PASSWORD", POSTGRESQL_CONTAINER.getPassword()
        );
        List<String> arguments = new ArrayList<>();
        arguments.add("--spring.profiles.active=prod");
        arguments.add("--spring.main.banner-mode=off");
        arguments.add("--logging.level.root=OFF");
        for (Map.Entry<String, String> entry : requiredDatabaseProperties.entrySet()) {
            if (!entry.getKey().equals(missingVariableName)) {
                arguments.add("--" + entry.getKey() + "=" + entry.getValue());
            }
        }
        return arguments.toArray(String[]::new);
    }
}
