package team.jit.technicalinterviewdemo.manualregression.suites;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 08 — admin category CRUD lifecycle. Creates one category, updates it, deletes it, and
 * verifies the follow-up read returns 404. Always runs cleanup even after a mid-flight failure.
 */
@SuiteName(
        value = "08-category-lifecycle-admin",
        requires = {"06-session-and-account"},
        requiresAdminIdentity = true)
public class Suite08CategoryLifecycleAdmin extends SuiteBase {

    private Long createdCategoryId;
    private Integer createdCategoryVersion;
    private boolean deleted;

    @Test
    @Order(1)
    void createCategory() {
        String name = "manual-regression-" + runTag();
        Response response = http().send(
                        "POST",
                        "/api/categories",
                        http().asAdmin().contentType(ContentType.JSON).body("{\"name\":\"" + name + "\"}"),
                        null,
                        Optional.of("create category"));
        assertThat(response.statusCode()).as("create category status").isIn(200, 201);
        Object idObject = response.jsonPath().get("id");
        assertThat(idObject).isNotNull();
        createdCategoryId = ((Number) idObject).longValue();
        Object versionObject = response.jsonPath().get("version");
        createdCategoryVersion = versionObject == null ? 0 : ((Number) versionObject).intValue();
        recordIdentifier("createdCategoryId", String.valueOf(createdCategoryId));
        recordIdentifier("createdCategoryName", name);
    }

    @Test
    @Order(2)
    void updateCreatedCategoryName() {
        if (createdCategoryId == null) {
            return;
        }
        String body = "{\"name\":\"manual-regression-" + runTag() + "-updated\"," + "\"version\":"
                + createdCategoryVersion + "}";
        int status = http().send(
                        "PUT",
                        "/api/categories/" + createdCategoryId,
                        http().asAdmin().contentType(ContentType.JSON).body(body),
                        null,
                        Optional.of("update category"))
                .statusCode();
        assertThat(status).isIn(200, 204);
    }

    @Test
    @Order(3)
    void deleteCategoryAndAssertNotFound() {
        if (createdCategoryId == null) {
            return;
        }
        int deleteStatus = http().send(
                        "DELETE",
                        "/api/categories/" + createdCategoryId,
                        http().asAdmin(),
                        null,
                        Optional.of("delete category"))
                .statusCode();
        assertThat(deleteStatus).isIn(200, 204);
        deleted = true;

        // The category-by-id read endpoint isn't part of the public contract, so verify deletion via
        // the list endpoint by filtering for the created name and asserting no match is returned.
        Response response = http().send(
                        "GET",
                        "/api/categories",
                        http().asAdmin(),
                        200,
                        Optional.of("verify deleted category is gone"));
        boolean stillPresent = response.asString().contains("manual-regression-" + runTag());
        assertThat(stillPresent)
                .as("deleted category should not appear in /api/categories")
                .isFalse();
    }

    @AfterAll
    void cleanupIfNeeded() {
        if (createdCategoryId != null && !deleted) {
            int status = http().send(
                            "DELETE",
                            "/api/categories/" + createdCategoryId,
                            http().asAdmin(),
                            null,
                            Optional.of("AfterAll cleanup of category " + createdCategoryId))
                    .statusCode();
            if (status != 200 && status != 204 && status != 404) {
                leftover("category id " + createdCategoryId + " (HTTP " + status + " on cleanup)");
            }
        }
    }
}
