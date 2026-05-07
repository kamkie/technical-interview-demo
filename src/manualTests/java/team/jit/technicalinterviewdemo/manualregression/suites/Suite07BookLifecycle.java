package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.HarnessResponse;
import team.jit.technicalinterviewdemo.manualregression.harness.ManualRegressionExtension;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 07 — admin book CRUD lifecycle.
 *
 * <p>Creates exactly one book against the first seeded category name captured by Suite 03, performs
 * a read-by-id, an update, a delete, and asserts the follow-up read returns 404. Always cleans up
 * even if the lifecycle assertions failed mid-flight.
 */
@SuiteName(
        value = "07-book-lifecycle",
        requires = {"03-public-book-and-category-reads", "06-session-and-account"},
        requiresAdminIdentity = true)
public class Suite07BookLifecycle extends SuiteBase {

    private Long createdBookId;
    private Integer createdBookVersion;
    private boolean deleted;

    @Test
    @Order(1)
    void createBook_returnsLocationOrEntity() {
        String categoryName = ManualRegressionExtension.lookupIdentifier(
                        "03-public-book-and-category-reads",
                        Suite03PublicBookAndCategoryReads.IDENTIFIER_FIRST_CATEGORY_NAME)
                .orElse(null);
        if (categoryName == null) {
            note("No seeded categoryName available; skipping book lifecycle");
            return;
        }
        String tag = runTag();
        String isbn = "978-MR-" + Integer.toUnsignedString(tag.hashCode());
        String body = "{\"title\":\"Manual Regression Book " + tag + "\","
                + "\"author\":\"Manual Regression\","
                + "\"isbn\":\"" + isbn + "\","
                + "\"publicationYear\":2025,"
                + "\"categories\":[\"" + categoryName + "\"]}";

        HarnessResponse response = http().send(
                        "POST",
                        "/api/books",
                        http().asAdmin().contentType("application/json").body(body),
                        null,
                        Optional.of("create book"));
        assertThat(response.statusCode()).as("create book status").isIn(200, 201);
        Object idObject = response.jsonPath().get("id");
        assertThat(idObject).as("created book id").isNotNull();
        createdBookId = ((Number) idObject).longValue();
        Object versionObject = response.jsonPath().get("version");
        createdBookVersion = versionObject == null ? 0 : ((Number) versionObject).intValue();
        recordIdentifier("createdBookId", String.valueOf(createdBookId));
        recordIdentifier("createdBookIsbn", isbn);
    }

    @Test
    @Order(2)
    void readCreatedBookById_returnsIt() {
        if (createdBookId == null) {
            note("Skipping read; create did not produce an id");
            return;
        }
        int status = http().send(
                        "GET", "/api/books/" + createdBookId, http().asAdmin(), 200, Optional.of("read created book"))
                .statusCode();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @Order(3)
    void updateCreatedBookTitle() {
        if (createdBookId == null) {
            note("Skipping update; no book id");
            return;
        }
        String body = "{\"title\":\"Manual Regression Book " + runTag() + " (updated)\","
                + "\"author\":\"Manual Regression\","
                + "\"version\":" + createdBookVersion + ","
                + "\"publicationYear\":2026}";
        HarnessResponse response = http().send(
                        "PUT",
                        "/api/books/" + createdBookId,
                        http().asAdmin().contentType("application/json").body(body),
                        null,
                        Optional.of("update created book"));
        assertThat(response.statusCode()).isIn(200, 204);
        Object versionObject = response.jsonPath().get("version");
        if (versionObject instanceof Number n) {
            createdBookVersion = n.intValue();
        }
    }

    @Test
    @Order(4)
    void deleteCreatedBookAndAssertNotFound() {
        if (createdBookId == null) {
            return;
        }
        int deleteStatus = http().send(
                        "DELETE",
                        "/api/books/" + createdBookId,
                        http().asAdmin(),
                        null,
                        Optional.of("delete created book"))
                .statusCode();
        assertThat(deleteStatus).as("delete book status").isIn(200, 204);
        deleted = true;
        int followUp = http().send(
                        "GET",
                        "/api/books/" + createdBookId,
                        http().asAdmin(),
                        404,
                        Optional.of("verify deleted book is gone"))
                .statusCode();
        assertThat(followUp).isEqualTo(404);
    }

    @AfterAll
    void cleanupIfNeeded() {
        if (createdBookId != null && !deleted) {
            int status = http().send(
                            "DELETE",
                            "/api/books/" + createdBookId,
                            http().asAdmin(),
                            null,
                            Optional.of("AfterAll cleanup of book " + createdBookId))
                    .statusCode();
            if (status != 200 && status != 204 && status != 404) {
                leftover("book id " + createdBookId + " (HTTP " + status + " on cleanup)");
            }
        }
    }
}
