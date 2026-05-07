package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 01 — public overview, smoke, and documentation endpoints.
 *
 * <p>Acts as the readiness probe for the entire run. If this suite fails or is blocked, the rest of
 * the harness reports the dependent suites as blocked rather than failing them.
 */
@SuiteName("01-public-overview-and-docs")
public class Suite01PublicOverviewAndDocs extends SuiteBase {

    @Test
    @Order(1)
    void rootOverview_isPubliclyReachable() {
        int status = http().send("GET", "/", http().anonymous(), 200, Optional.of("public root overview"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @Order(2)
    void helloSmoke_isPubliclyReachable() {
        int status = http().send("GET", "/hello", http().anonymous(), 200, Optional.of("public hello smoke"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @Order(3)
    void docsRedirect_returns3xx() {
        int status = http().send("GET", "/docs", http().anonymous(), null, Optional.of("docs redirect"))
                .statusCode();
        assertThat(status)
                .as("/docs should redirect or serve directly")
                .isIn(200, 301, 302, 303, 307, 308);
    }

    @Test
    @Order(4)
    void openApiJson_isPubliclyReachable() {
        int status = http().send("GET", "/v3/api-docs", http().anonymous(), 200, Optional.of("OpenAPI JSON"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }
}
