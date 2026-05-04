package team.jit.technicalinterviewdemo.technical.info;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record TechnicalOverviewResponse(
        BuildDetails build,
        GitDetails git,
        RuntimeDetails runtime,
        Map<String, String> dependencies,
        ConfigurationDetails configuration
) {

    public record BuildDetails(
            String name,
            String group,
            String artifact,
            String version,
            Instant time
    ) {
    }

    public record GitDetails(
            String branch,
            String commitId,
            String shortCommitId,
            Instant commitTime
    ) {
    }

    public record RuntimeDetails(
            String applicationName,
            String javaVersion,
            String javaVendor,
            List<String> activeProfiles
    ) {
    }

    public record ConfigurationDetails(
            PaginationDetails pagination,
            SessionDetails session,
            ObservabilityDetails observability,
            DocumentationDetails documentation,
            SecurityDetails security,
            ShutdownDetails shutdown
    ) {
    }

    public record PaginationDetails(
            int defaultPageSize,
            int maxPageSize
    ) {
    }

    public record SessionDetails(
            String storeType,
            String timeout,
            String cookieName,
            boolean cookieHttpOnly,
            String cookieSameSite
    ) {
    }

    public record ObservabilityDetails(
            List<String> exposedEndpoints,
            boolean healthProbesEnabled,
            double tracingSamplingProbability
    ) {
    }

    public record DocumentationDetails(
            String html,
            String openApiJson,
            String openApiYaml,
            String openApiVersion
    ) {
    }

    public record SecurityDetails(
            boolean csrfEnabled,
            String csrfCookieName,
            String csrfHeaderName,
            boolean oauthProfileActive,
            String publicApiPathPattern,
            String oauthAuthorizationBasePath,
            String oauthCallbackPathTemplate,
            String forwardHeadersStrategy,
            AbuseProtectionDetails abuseProtection
    ) {
    }

    public record AbuseProtectionDetails(
            String owner,
            String loginBootstrapPathTemplate,
            List<String> loginBootstrapControls,
            String unsafeWritePathPattern,
            List<String> unsafeWriteExamples,
            List<String> unsafeWriteControls
    ) {
    }

    public record ShutdownDetails(
            String serverShutdown,
            String timeoutPerShutdownPhase
    ) {
    }
}

