package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 05 — anonymous-access negative checks.
 *
 * <p>Confirms that protected endpoints reject anonymous callers with 401 Problem Details, and that
 * unsafe writes from a session with no CSRF token are blocked. The suite intentionally never sends
 * a session cookie or CSRF header so the assertions remain meaningful.
 */
@SuiteName(value = "05-anonymous-negative-access", requires = {"01-public-overview-and-docs"})
public class Suite05AnonymousNegativeAccess extends SuiteBase {

    @Test
    @Order(1)
    void getAccount_anonymous_returns401() {
        int status = http().send("GET", "/api/account", http().anonymous(), 401, Optional.of("anonymous /api/account"))
                .statusCode();
        assertThat(status).isEqualTo(401);
    }

    @Test
    @Order(2)
    void listUsers_anonymous_returns401() {
        int status = http().send(
                        "GET",
                        "/api/admin/users",
                        http().anonymous(),
                        401,
                        Optional.of("anonymous /api/admin/users"))
                .statusCode();
        assertThat(status).isEqualTo(401);
    }

    @Test
    @Order(3)
    void listAuditLogs_anonymous_returns401() {
        int status = http().send(
                        "GET",
                        "/api/admin/audit-logs",
                        http().anonymous(),
                        401,
                        Optional.of("anonymous /api/admin/audit-logs"))
                .statusCode();
        assertThat(status).isEqualTo(401);
    }

    @Test
    @Order(4)
    void operatorSurface_anonymous_returns401() {
        int status = http().send(
                        "GET",
                        "/api/admin/operator-surface",
                        http().anonymous(),
                        401,
                        Optional.of("anonymous operator surface"))
                .statusCode();
        assertThat(status).isEqualTo(401);
    }

    @Test
    @Order(5)
    void createCategory_anonymous_isRejected() {
        // Without a session cookie or CSRF token, the request must not be accepted; expected outcomes
        // are 401 (no session) or 403 (CSRF rejection if a Spring filter answers first).
        int status = http().send(
                        "POST",
                        "/api/categories",
                        http().anonymous().contentType("application/json").body("{\"name\":\"manual-regression-anon\"}"),
                        null,
                        Optional.of("anonymous POST /api/categories"))
                .statusCode();
        assertThat(status).isIn(401, 403);
    }
}
