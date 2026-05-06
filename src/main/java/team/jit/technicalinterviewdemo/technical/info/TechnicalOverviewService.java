package team.jit.technicalinterviewdemo.technical.info;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.hibernate.Version;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.stereotype.Service;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;
import team.jit.technicalinterviewdemo.technical.security.SecuritySettingsProperties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TechnicalOverviewService {

    static final String DOCS_PATH = "/docs";
    static final String OPEN_API_JSON_PATH = "/v3/api-docs";
    static final String OPEN_API_YAML_PATH = "/v3/api-docs.yaml";
    static final String PUBLIC_API_PATH_PATTERN = "/api/**";

    private final BuildProperties buildProperties;
    private final GitProperties gitProperties;
    private final Environment environment;

    public TechnicalOverviewResponse getOverview() {
        List<String> activeProfiles = List.copyOf(Arrays.asList(environment.getActiveProfiles()));
        TechnicalOverviewResponse response = new TechnicalOverviewResponse(
                new TechnicalOverviewResponse.BuildDetails(
                        buildProperties.getName(), buildProperties.getGroup(), buildProperties.getArtifact(), buildProperties.getVersion(), buildProperties.getTime()
                ), new TechnicalOverviewResponse.GitDetails(
                        gitProperties.getBranch(), gitProperties.getCommitId(), gitProperties.getShortCommitId(), gitProperties.getCommitTime()
                ), new TechnicalOverviewResponse.RuntimeDetails(
                        environment.getProperty("spring.application.name", "technical-interview-demo"), System.getProperty("java.version", "unknown"), System.getProperty("java.vendor", "unknown"), activeProfiles
                ), dependencyVersions(), new TechnicalOverviewResponse.ConfigurationDetails(
                        new TechnicalOverviewResponse.PaginationDetails(
                                intProperty("spring.data.web.pageable.default-page-size", 20), intProperty("spring.data.web.pageable.max-page-size", 100)
                        ), new TechnicalOverviewResponse.SessionDetails(
                                property("spring.session.store-type", "unknown"), property("server.servlet.session.timeout", "unknown"), property("server.servlet.session.cookie.name", "unknown"), booleanProperty("server.servlet.session.cookie.http-only", true), property("server.servlet.session.cookie.same-site", "unknown")
                        ), new TechnicalOverviewResponse.ObservabilityDetails(
                                listProperty("management.endpoints.web.exposure.include"), booleanProperty("management.endpoint.health.probes.enabled", false), doubleProperty("management.tracing.sampling.probability", 0.0d)
                        ), new TechnicalOverviewResponse.DocumentationDetails(
                                DOCS_PATH, OPEN_API_JSON_PATH, OPEN_API_YAML_PATH, property("springdoc.api-docs.version", "unknown")
                        ), new TechnicalOverviewResponse.SecurityDetails(
                                true, SameSiteCsrfContract.COOKIE_NAME, SameSiteCsrfContract.HEADER_NAME, activeProfiles.contains("oauth"), PUBLIC_API_PATH_PATTERN, SecuritySettingsProperties.OAuth.AUTHORIZATION_BASE_URI, SecuritySettingsProperties.OAuth.CALLBACK_BASE_URI + "/{registrationId}", property("server.forward-headers-strategy", "none"), new TechnicalOverviewResponse.AbuseProtectionDetails(
                                        "edge-or-gateway", SecuritySettingsProperties.OAuth.AUTHORIZATION_BASE_URI + "/{registrationId}", List.of("burst-rate-limiting", "challenge-or-block-suspicious-clients"), PUBLIC_API_PATH_PATTERN, List.of(
                                                "/api/session/logout", "/api/books", "/api/categories", "/api/localizations", "/api/account/language"
                                        ), List.of("per-client-throttling", "request-size-enforcement", "rejection-visibility")
                                )
                        ), new TechnicalOverviewResponse.ShutdownDetails(
                                property("server.shutdown", "unknown"), property("spring.lifecycle.timeout-per-shutdown-phase", "unknown")
                        )
                )
        );
        return response;
    }

    private Map<String, String> dependencyVersions() {
        Map<String, String> versions = new LinkedHashMap<>();
        versions.put("springBoot", SpringBootVersion.getVersion());
        versions.put("springFramework", SpringVersion.getVersion());
        versions.put("springSecurity", SpringSecurityCoreVersion.getVersion());
        versions.put("springSessionJdbc", packageVersion(JdbcIndexedSessionRepository.class));
        versions.put("hibernate", Version.getVersionString());
        versions.put("flyway", packageVersion(Flyway.class));
        versions.put("postgresqlDriver", packageVersion("org.postgresql.Driver"));
        versions.put("caffeine", packageVersion(Caffeine.class));
        versions.put("micrometer", packageVersion(MeterRegistry.class));
        versions.put("springdoc", packageVersion(SpringDocConfigProperties.class));
        return versions;
    }

    private List<String> listProperty(String key) {
        return Arrays.stream(property(key, "").split(",")).map(String::trim).filter(value -> !value.isEmpty()).toList();
    }

    private String property(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    private int intProperty(String key, int defaultValue) {
        return environment.getProperty(key, Integer.class, defaultValue);
    }

    private boolean booleanProperty(String key, boolean defaultValue) {
        return environment.getProperty(key, Boolean.class, defaultValue);
    }

    private double doubleProperty(String key, double defaultValue) {
        return environment.getProperty(key, Double.class, defaultValue);
    }

    private String packageVersion(Class<?> type) {
        Package packageMetadata = type.getPackage();
        if (packageMetadata == null || packageMetadata.getImplementationVersion() == null) {
            return "unknown";
        }
        return packageMetadata.getImplementationVersion();
    }

    private String packageVersion(String className) {
        try {
            Class<?> type = Class.forName(className);
            return packageVersion(type);
        } catch (ClassNotFoundException exception) {
            return "unknown";
        }
    }
}
