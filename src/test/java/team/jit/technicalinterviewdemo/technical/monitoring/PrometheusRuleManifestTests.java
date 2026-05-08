package team.jit.technicalinterviewdemo.technical.monitoring;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PrometheusRuleManifestTests {

    @Test
    void authenticationFailureAlertCoversProtectedBookWrites() throws IOException {
        String rule = Files.readString(Path.of("infra/k8s/monitoring/prometheus-rule.yaml"));
        String alertRule = rule.substring(
                rule.indexOf("- alert: TechnicalInterviewDemoAuthenticationFailuresElevated"),
                rule.indexOf("- alert: TechnicalInterviewDemoSessionBackedAccountErrors"));

        assertThat(alertRule)
                .contains("method=~\"POST|PUT|DELETE\"")
                .contains("uri=~\"/api/books|/api/books/.+|/api/categories")
                .contains("status=~\"401|403\"");
    }
}
