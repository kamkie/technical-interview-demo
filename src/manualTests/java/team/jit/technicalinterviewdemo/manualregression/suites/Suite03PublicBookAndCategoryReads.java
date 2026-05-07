package team.jit.technicalinterviewdemo.manualregression.suites;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.manualregression.harness.HarnessJsonPath;
import team.jit.technicalinterviewdemo.manualregression.harness.HarnessResponse;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 03 — public, anonymous reads of the seeded book and category catalog.
 *
 * <p>Captures the first seeded book id and the first seeded category id/name and stores them on the
 * report as generated identifiers so downstream lifecycle suites can reuse them.
 */
@SuiteName(
        value = "03-public-book-and-category-reads",
        requires = {"01-public-overview-and-docs"})
public class Suite03PublicBookAndCategoryReads extends SuiteBase {

    static final String IDENTIFIER_FIRST_BOOK_ID = "firstBookId";
    static final String IDENTIFIER_FIRST_CATEGORY_ID = "firstCategoryId";
    static final String IDENTIFIER_FIRST_CATEGORY_NAME = "firstCategoryName";

    @Test
    @Order(1)
    void listBooksWithPagination_returnsContent() {
        HarnessResponse response = http().send(
                        "GET",
                        "/api/books?page=0&size=5&sort=title,asc",
                        http().anonymous(),
                        200,
                        Optional.of("public book list"));
        List<Object> content = response.jsonPath().getList("content");
        if (content == null || content.isEmpty()) {
            note("Books list is empty; demo-data Flyway migrations may not be applied");
        } else {
            recordIdentifier(
                    IDENTIFIER_FIRST_BOOK_ID, String.valueOf(response.jsonPath().get("content[0].id")));
        }
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(2)
    void listCategories_returnsContent() {
        HarnessResponse response =
                http().send("GET", "/api/categories", http().anonymous(), 200, Optional.of("public category list"));
        HarnessJsonPath body = response.jsonPath();
        // Categories endpoint returns either a list or a paginated wrapper depending on the contract;
        // try paginated first, fall back to a flat list.
        Object pageContent = body.get("content");
        List<Object> items = pageContent instanceof List<?> list ? List.copyOf(list) : body.getList("$");
        if (items == null || items.isEmpty()) {
            note("Categories list is empty; demo-data Flyway migrations may not be applied");
        } else {
            String idPath = pageContent != null ? "content[0].id" : "[0].id";
            String namePath = pageContent != null ? "content[0].name" : "[0].name";
            recordIdentifier(IDENTIFIER_FIRST_CATEGORY_ID, String.valueOf(body.get(idPath)));
            recordIdentifier(IDENTIFIER_FIRST_CATEGORY_NAME, String.valueOf(body.get(namePath)));
        }
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
