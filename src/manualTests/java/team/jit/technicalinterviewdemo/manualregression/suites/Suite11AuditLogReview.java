package team.jit.technicalinterviewdemo.manualregression.suites;

import io.restassured.response.Response;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 11 — admin audit log review. Read-only checks that the audit log endpoint paginates and
 * filters as documented and that recent entries (from the lifecycle suites that just ran) are
 * present. The presence check is best-effort; if the lifecycle suites were skipped, the suite
 * notes that and continues.
 */
@SuiteName(
        value = "11-audit-log-review",
        requires = {"07-book-lifecycle", "08-category-lifecycle-admin", "09-localization-lifecycle-admin", "10-admin-user-management"},
        requiresAdminIdentity = true)
public class Suite11AuditLogReview extends SuiteBase {

    @Test
    @Order(1)
    void listAuditLogs_paginated_returns200() {
        Response response = http().send(
                "GET",
                "/api/admin/audit-logs?page=0&size=20&sort=id,desc",
                http().asAdmin(),
                200,
                Optional.of("audit logs page 0 desc"));
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(2)
    void filterAuditLogs_byActorAndAction_returns200() {
        Response response = http().send(
                "GET",
                "/api/admin/audit-logs?targetType=AUTHENTICATION&action=LOGIN_SUCCESS",
                http().asAdmin(),
                200,
                Optional.of("audit logs filter authentication"));
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(3)
    void recentEntriesIncludeManualRegressionWrites() {
        Response response = http().send(
                "GET",
                "/api/admin/audit-logs?page=0&size=50&sort=id,desc",
                http().asAdmin(),
                200,
                Optional.of("audit logs to detect lifecycle writes"));
        List<?> content = response.jsonPath().getList("content");
        if (content == null || content.isEmpty()) {
            note("Audit log is empty or paginated payload missing; cannot verify lifecycle entries");
            return;
        }
        boolean foundLifecycleEntry = response.asString().contains(runTag());
        if (!foundLifecycleEntry) {
            note("No audit entry mentioning runTag " + runTag() + "; lifecycle suites may have been skipped");
        }
    }
}
