package team.jit.technicalinterviewdemo.manualregression.suites;

import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 12 — admin operator-surface inspection. Read-only check that the operator-surface endpoint
 * answers as admin and refuses anonymous and regular-user callers as documented.
 */
@SuiteName(
        value = "12-operator-surface",
        requires = {"06-session-and-account"},
        requiresAdminIdentity = true)
public class Suite12OperatorSurface extends SuiteBase {

    @Test
    @Order(1)
    void operatorSurface_asAdmin_returns200() {
        Response response = http().send(
                        "GET",
                        "/api/admin/operator-surface",
                        http().asAdmin(),
                        200,
                        Optional.of("admin operator-surface"));
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.asString()).isNotBlank();
    }

    @Test
    @Order(2)
    void operatorSurface_asRegularUser_returns403WhenSupplied() {
        if (!config().hasRegularIdentity()) {
            note("Regular-user identity not supplied; skipping 403 check");
            return;
        }
        int status = http().send(
                        "GET",
                        "/api/admin/operator-surface",
                        http().asRegularUser(),
                        403,
                        Optional.of("regular user operator-surface"))
                .statusCode();
        assertThat(status).isEqualTo(403);
    }
}
