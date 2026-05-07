package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 02 — actuator endpoints exposed for trusted deployment scraping.
 *
 * <p>Health endpoints must report {@code UP}; the Prometheus scrape endpoint must respond 200 with
 * a non-empty body. The harness does not assert specific metric names because those are validated
 * by the automated integration suite.
 */
@SuiteName(
        value = "02-actuator-and-observability",
        requires = {"01-public-overview-and-docs"})
public class Suite02ActuatorAndObservability extends SuiteBase {

    @Test
    @Order(1)
    void actuatorInfo_returns200() {
        int status = http().send("GET", "/actuator/info", http().anonymous(), 200, Optional.of("actuator info"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @Order(2)
    void actuatorHealth_isUp() {
        String status = http().send("GET", "/actuator/health", http().anonymous(), 200, Optional.of("aggregate health"))
                .jsonPath()
                .getString("status");
        assertThat(status).isEqualTo("UP");
    }

    @Test
    @Order(3)
    void livenessAndReadiness_areUp() {
        String liveness = http().send(
                        "GET", "/actuator/health/liveness", http().anonymous(), 200, Optional.of("liveness"))
                .jsonPath()
                .getString("status");
        String readiness = http().send(
                        "GET", "/actuator/health/readiness", http().anonymous(), 200, Optional.of("readiness"))
                .jsonPath()
                .getString("status");
        assertThat(liveness).isEqualTo("UP");
        assertThat(readiness).isEqualTo("UP");
    }

    @Test
    @Order(4)
    void prometheusScrape_servesNonEmptyBody() {
        String body = http().send(
                        "GET", "/actuator/prometheus", http().anonymous(), 200, Optional.of("prometheus scrape"))
                .asString();
        assertThat(body).isNotBlank();
    }
}
