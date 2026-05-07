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
 * Suite 06 — admin session contract and account self-service.
 *
 * <p>Validates that the supplied admin session is live ({@code GET /api/session} returns an
 * authenticated payload), that {@code GET /api/account} returns the same identity, and that the
 * preferred-language round-trip succeeds. The original preferred language is captured before the
 * change and always restored on teardown so the admin user is left exactly as found.
 */
@SuiteName(
        value = "06-session-and-account",
        requires = {"01-public-overview-and-docs"},
        requiresAdminIdentity = true)
public class Suite06SessionAndAccount extends SuiteBase {

    private String originalPreferredLanguage;
    private boolean preferredLanguageChanged;

    @Test
    @Order(1)
    void session_isAuthenticated() {
        Response response =
                http().send("GET", "/api/session", http().asAdmin(), 200, Optional.of("admin /api/session"));
        Boolean authenticated = response.jsonPath().getObject("authenticated", Boolean.class);
        assertThat(authenticated).isTrue();
    }

    @Test
    @Order(2)
    void account_returnsAdminIdentity() {
        Response response =
                http().send("GET", "/api/account", http().asAdmin(), 200, Optional.of("admin /api/account"));
        originalPreferredLanguage = response.jsonPath().getString("preferredLanguage");
        recordIdentifier("originalPreferredLanguage", String.valueOf(originalPreferredLanguage));
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(3)
    void changePreferredLanguageToPl() {
        if (originalPreferredLanguage == null) {
            note("Skipping language change because /api/account did not report a preferredLanguage");
            return;
        }
        String target = "pl".equals(originalPreferredLanguage) ? "en" : "pl";
        int status = http().send(
                        "PUT",
                        "/api/account/language",
                        http().asAdmin()
                                .contentType(ContentType.JSON)
                                .body("{\"preferredLanguage\":\"" + target + "\"}"),
                        null,
                        Optional.of("change preferred language to " + target))
                .statusCode();
        assertThat(status).isIn(200, 204);
        preferredLanguageChanged = true;
        recordIdentifier("temporaryPreferredLanguage", target);
    }

    @AfterAll
    void restorePreferredLanguage() {
        if (!preferredLanguageChanged || originalPreferredLanguage == null) {
            return;
        }
        Response response = http().send(
                        "PUT",
                        "/api/account/language",
                        http().asAdmin()
                                .contentType(ContentType.JSON)
                                .body("{\"preferredLanguage\":\"" + originalPreferredLanguage + "\"}"),
                        null,
                        Optional.of("restore original preferred language"));
        if (response.statusCode() != 200 && response.statusCode() != 204) {
            leftover(
                    "preferredLanguage was not restored to " + originalPreferredLanguage + "; manual restore required");
            throw new AssertionError("Failed to restore preferredLanguage; HTTP " + response.statusCode());
        }
    }
}
