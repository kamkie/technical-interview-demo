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
 * Suite 04 — public, anonymous reads against the localization catalog. Captures the first matching
 * {@code error.book.not_found / en} message id so a downstream lifecycle suite can verify the
 * read-by-id path without depending on insertion order.
 */
@SuiteName(
        value = "04-public-localization-reads",
        requires = {"01-public-overview-and-docs"})
public class Suite04PublicLocalizationReads extends SuiteBase {

    static final String IDENTIFIER_FIRST_LOCALIZATION_ID = "firstLocalizationId";

    @Test
    @Order(1)
    void listLocalizationsWithPagination_returns200() {
        Response response = http().send(
                        "GET",
                        "/api/localizations?page=0&size=5&sort=messageKey,asc&sort=language,asc",
                        http().anonymous(),
                        200,
                        Optional.of("paginated localization list"));
        List<Object> content = response.jsonPath().getList("content");
        if (content == null || content.isEmpty()) {
            note("Localization list is empty; demo-data Flyway migrations may not be applied");
        }
    }

    @Test
    @Order(2)
    void filterByKnownKey_returnsMatchAndCapturesId() {
        Response response = http().send(
                        "GET",
                        "/api/localizations?messageKey=error.book.not_found&language=en",
                        http().anonymous(),
                        200,
                        Optional.of("filter by known message key"));
        List<Object> content = response.jsonPath().getList("content");
        if (content == null || content.isEmpty()) {
            note("Expected seeded localization 'error.book.not_found / en' is missing");
            return;
        }
        Object id = response.jsonPath().get("content[0].id");
        recordIdentifier(IDENTIFIER_FIRST_LOCALIZATION_ID, String.valueOf(id));
        assertThat(id).isNotNull();
    }
}
