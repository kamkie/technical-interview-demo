package team.jit.technicalinterviewdemo.manualregression.suites;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import team.jit.technicalinterviewdemo.manualregression.harness.SuiteBase;
import team.jit.technicalinterviewdemo.manualregression.harness.SuiteName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite 10 — admin governance over the persisted user catalog.
 *
 * <p>Always:
 *
 * <ul>
 *   <li>lists users as admin (200) and reports a leftover note if the regular user is missing
 *   <li>asserts the list endpoint rejects the regular user (403) when a regular identity is supplied
 * </ul>
 *
 * <p>The role-grant round trip only runs when both a regular-user id and a regular-user identity
 * are supplied: the suite captures the original roles, replaces them with an additional role for
 * the round trip, then always restores the original set on teardown. If the restore fails, the
 * created identifier is recorded as a leftover so the executor can clean it manually.
 */
@SuiteName(
        value = "10-admin-user-management",
        requires = {"06-session-and-account"},
        requiresAdminIdentity = true)
public class Suite10AdminUserManagement extends SuiteBase {

    private List<String> originalRoles;
    private boolean rolesChanged;

    @Test
    @Order(1)
    void listUsersAsAdmin_returnsContent() {
        Response response = http().send(
                "GET", "/api/admin/users", http().asAdmin(), 200, Optional.of("admin /api/admin/users"));
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(2)
    void listUsersAsRegularUser_returns403WhenSupplied() {
        if (!config().hasRegularIdentity()) {
            note("Regular-user identity not supplied; skipping 403 check");
            return;
        }
        int status = http().send(
                        "GET",
                        "/api/admin/users",
                        http().asRegularUser(),
                        403,
                        Optional.of("regular user /api/admin/users"))
                .statusCode();
        assertThat(status).isEqualTo(403);
    }

    @Test
    @Order(3)
    void grantAndRestoreRolesForRegularUser() {
        if (config().regularUserId().isEmpty()) {
            note("regularUserId not supplied; skipping role round-trip");
            return;
        }
        String userId = config().regularUserId().orElseThrow();

        // Capture original roles by listing all users and finding this id.
        Response listing = http().send(
                "GET",
                "/api/admin/users?page=0&size=200",
                http().asAdmin(),
                200,
                Optional.of("locate user " + userId + " for role capture"));
        Object usersNode = listing.jsonPath().get("content");
        List<?> users = usersNode instanceof List<?> l ? l : listing.jsonPath().getList("$");
        if (users == null) {
            note("Could not locate user list payload; skipping role round-trip");
            return;
        }
        originalRoles = users.stream()
                .filter(java.util.Map.class::isInstance)
                .map(java.util.Map.class::cast)
                .filter(u -> String.valueOf(u.get("id")).equals(userId))
                .map(u -> u.get("roles"))
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .findFirst()
                .map(roles -> roles.stream().map(String::valueOf).toList())
                .orElse(null);
        if (originalRoles == null) {
            note("User id " + userId + " not found in admin listing; skipping role round-trip");
            return;
        }
        recordIdentifier("targetUserId", userId);
        recordIdentifier("originalRoles", originalRoles.toString());

        // Replace with original + AUDIT (no-op if AUDIT already present).
        java.util.Set<String> grantedSet = new java.util.LinkedHashSet<>(originalRoles);
        grantedSet.add("AUDIT");
        String body = "{\"roles\":" + toJsonStringArray(grantedSet)
                + ",\"reason\":\"Manual regression round-trip for " + runTag() + "\"}";
        int grantStatus = http().send(
                        "PUT",
                        "/api/admin/users/" + userId + "/roles",
                        http().asAdmin().contentType(ContentType.JSON).body(body),
                        null,
                        Optional.of("grant roles round-trip"))
                .statusCode();
        if (grantStatus == 200 || grantStatus == 204) {
            rolesChanged = true;
        } else {
            note("Role grant returned " + grantStatus + "; not attempting restore");
        }
    }

    @AfterAll
    void restoreRolesIfChanged() {
        if (!rolesChanged || originalRoles == null) {
            return;
        }
        String userId = config().regularUserId().orElseThrow();
        String body = "{\"roles\":" + toJsonStringArray(originalRoles)
                + ",\"reason\":\"Manual regression round-trip restore for " + runTag() + "\"}";
        int status = http().send(
                        "PUT",
                        "/api/admin/users/" + userId + "/roles",
                        http().asAdmin().contentType(ContentType.JSON).body(body),
                        null,
                        Optional.of("restore original roles"))
                .statusCode();
        if (status != 200 && status != 204) {
            leftover("user " + userId + " roles not restored to " + originalRoles + " (HTTP " + status + ")");
            throw new AssertionError("Failed to restore roles; HTTP " + status);
        }
    }

    private static String toJsonStringArray(Iterable<String> values) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String v : values) {
            if (!first) {
                sb.append(',');
            }
            sb.append('"').append(v.replace("\"", "\\\"")).append('"');
            first = false;
        }
        return sb.append(']').toString();
    }
}
