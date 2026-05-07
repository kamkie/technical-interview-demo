package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.HarnessResponse;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 09 — admin localization message lifecycle. Creates one message under {@code en}, updates
 * its text, deletes it, and asserts the follow-up read returns 404.
 */
@SuiteName(
        value = "09-localization-lifecycle-admin",
        requires = {"06-session-and-account"},
        requiresAdminIdentity = true)
public class Suite09LocalizationLifecycleAdmin extends SuiteBase {

    private Long createdLocalizationId;
    private Integer createdLocalizationVersion;
    private boolean deleted;

    @Test
    @Order(1)
    void createLocalization() {
        String key = "manual.regression." + runTag().replace('-', '.');
        String body = "{\"messageKey\":\"" + key + "\","
                + "\"language\":\"en\","
                + "\"messageText\":\"Manual regression marker for " + runTag() + "\","
                + "\"description\":\"Created by the manual regression harness\"}";
        HarnessResponse response = http().send(
                        "POST",
                        "/api/localizations",
                        http().asAdmin().contentType("application/json").body(body),
                        null,
                        Optional.of("create localization"));
        assertThat(response.statusCode()).isIn(200, 201);
        Object idObject = response.jsonPath().get("id");
        assertThat(idObject).isNotNull();
        createdLocalizationId = ((Number) idObject).longValue();
        Object versionObject = response.jsonPath().get("version");
        createdLocalizationVersion = versionObject == null ? 0 : ((Number) versionObject).intValue();
        recordIdentifier("createdLocalizationId", String.valueOf(createdLocalizationId));
        recordIdentifier("createdLocalizationKey", key);
    }

    @Test
    @Order(2)
    void readCreatedLocalizationById() {
        if (createdLocalizationId == null) {
            return;
        }
        int status = http().send(
                        "GET",
                        "/api/localizations/" + createdLocalizationId,
                        http().asAdmin(),
                        200,
                        Optional.of("read created localization"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @Order(3)
    void updateCreatedLocalizationText() {
        if (createdLocalizationId == null) {
            return;
        }
        String key = "manual.regression." + runTag().replace('-', '.');
        String body = "{\"messageKey\":\"" + key + "\","
                + "\"language\":\"en\","
                + "\"messageText\":\"Manual regression marker for " + runTag() + " (updated)\","
                + "\"description\":\"Updated by the manual regression harness\","
                + "\"version\":" + createdLocalizationVersion + "}";
        int status = http().send(
                        "PUT",
                        "/api/localizations/" + createdLocalizationId,
                        http().asAdmin().contentType("application/json").body(body),
                        null,
                        Optional.of("update created localization"))
                .statusCode();
        assertThat(status).isIn(200, 204);
    }

    @Test
    @Order(4)
    void deleteLocalizationAndAssertNotFound() {
        if (createdLocalizationId == null) {
            return;
        }
        int deleteStatus = http().send(
                        "DELETE",
                        "/api/localizations/" + createdLocalizationId,
                        http().asAdmin(),
                        null,
                        Optional.of("delete created localization"))
                .statusCode();
        assertThat(deleteStatus).isIn(200, 204);
        deleted = true;
        int followUp = http().send(
                        "GET",
                        "/api/localizations/" + createdLocalizationId,
                        http().asAdmin(),
                        404,
                        Optional.of("verify deleted localization is gone"))
                .statusCode();
        assertThat(followUp).isEqualTo(404);
    }

    @AfterAll
    void cleanupIfNeeded() {
        if (createdLocalizationId != null && !deleted) {
            int status = http().send(
                            "DELETE",
                            "/api/localizations/" + createdLocalizationId,
                            http().asAdmin(),
                            null,
                            Optional.of("AfterAll cleanup of localization " + createdLocalizationId))
                    .statusCode();
            if (status != 200 && status != 204 && status != 404) {
                leftover("localization id " + createdLocalizationId + " (HTTP " + status + " on cleanup)");
            }
        }
    }
}
