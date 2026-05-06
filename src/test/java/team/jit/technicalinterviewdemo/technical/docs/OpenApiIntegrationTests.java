package team.jit.technicalinterviewdemo.technical.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.testing.AbstractRandomPortIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RandomPortIntegrationSpringBootTest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RandomPortIntegrationSpringBootTest
class OpenApiIntegrationTests extends AbstractRandomPortIntegrationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void openApiJsonEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs");

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").startsWith("application/json"));
        assertTrue(response.body().contains("\"openapi\":\"3."));
        assertTrue(response.body().contains("\"/api/books\""));
    }

    @Test
    void openApiYamlEndpointIsExposed() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs.yaml");

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("content-type").orElse("").contains("openapi"));
        assertTrue(response.body().contains("openapi: 3."));
        assertTrue(response.body().contains("/api/books:"));
    }

    @Test
    void openApiJsonDocumentsSecurityPaginationAndSchemas() throws IOException, InterruptedException {
        JsonNode openApi = fetchOpenApiJson();

        assertEquals("technical-interview-demo API", openApi.at("/info/title").asText());
        assertEquals(
                "Machine-readable contract for the demo application's supported external /api/** surface."
                        + " Internal-only overview, documentation, OpenAPI publication, and actuator validation"
                        + " paths are intentionally excluded.",
                openApi.at("/info/description").asText());
        assertTrue(openApi.at("/paths/~1/get").isMissingNode());
        assertTrue(openApi.at("/paths/~1hello/get").isMissingNode());
        assertEquals(
                "apiKey",
                openApi.at("/components/securitySchemes/sessionCookie/type").asText());
        assertEquals(
                "cookie",
                openApi.at("/components/securitySchemes/sessionCookie/in").asText());
        assertEquals(
                "technical-interview-demo-session",
                openApi.at("/components/securitySchemes/sessionCookie/name").asText());
        assertEquals(
                "Authenticated browser session cookie used by protected operations. It is established through a"
                        + " configured identity provider login path under"
                        + " /api/session/oauth2/authorization/{registrationId} when the optional oauth profile is"
                        + " active. Unsafe browser writes also require the X-XSRF-TOKEN request header"
                        + " mirrored from the readable XSRF-TOKEN cookie.",
                openApi.at("/components/securitySchemes/sessionCookie/description")
                        .asText());

        JsonNode createBookSecurity = openApi.at("/paths/~1api~1books/post/security");
        assertFalse(createBookSecurity.isMissingNode());
        assertEquals("sessionCookie", createBookSecurity.get(0).fieldNames().next());
        assertTrue(openApi.at("/paths/~1api~1books/post/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        JsonNode accountSecurity = openApi.at("/paths/~1api~1account/get/security");
        assertFalse(accountSecurity.isMissingNode());
        assertEquals("sessionCookie", accountSecurity.get(0).fieldNames().next());
        assertTrue(openApi.at("/paths/~1api~1account~1language/put/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        assertFalse(openApi.at("/paths/~1api~1session/get").isMissingNode());
        assertTrue(openApi.at("/paths/~1api~1session/get/security").isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1session~1logout/post").isMissingNode());
        assertTrue(openApi.at("/paths/~1api~1session~1logout/post/security").isMissingNode());
        assertTrue(openApi.at("/paths/~1api~1session~1logout/post/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        assertTrue(openApi.at("/paths/~1api~1audit-logs/get").isMissingNode());
        JsonNode auditLogSecurity = openApi.at("/paths/~1api~1admin~1audit-logs/get/security");
        assertFalse(auditLogSecurity.isMissingNode());
        assertEquals("sessionCookie", auditLogSecurity.get(0).fieldNames().next());

        assertTrue(openApi.at("/paths/~1api~1operator~1surface/get").isMissingNode());
        JsonNode operatorSurfaceSecurity = openApi.at("/paths/~1api~1admin~1operator-surface/get/security");
        assertFalse(operatorSurfaceSecurity.isMissingNode());
        assertEquals(
                "sessionCookie", operatorSurfaceSecurity.get(0).fieldNames().next());

        JsonNode adminUsersSecurity = openApi.at("/paths/~1api~1admin~1users/get/security");
        assertFalse(adminUsersSecurity.isMissingNode());
        assertEquals("sessionCookie", adminUsersSecurity.get(0).fieldNames().next());

        JsonNode replaceManagedRolesSecurity = openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/security");
        assertFalse(replaceManagedRolesSecurity.isMissingNode());
        assertEquals(
                "sessionCookie", replaceManagedRolesSecurity.get(0).fieldNames().next());
        assertTrue(openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        JsonNode updateCategorySecurity = openApi.at("/paths/~1api~1categories~1{id}/put/security");
        assertFalse(updateCategorySecurity.isMissingNode());
        assertEquals("sessionCookie", updateCategorySecurity.get(0).fieldNames().next());
        assertTrue(openApi.at("/paths/~1api~1categories~1{id}/put/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        JsonNode deleteCategorySecurity = openApi.at("/paths/~1api~1categories~1{id}/delete/security");
        assertFalse(deleteCategorySecurity.isMissingNode());
        assertEquals("sessionCookie", deleteCategorySecurity.get(0).fieldNames().next());
        assertTrue(openApi.at("/paths/~1api~1categories~1{id}/delete/parameters")
                .findValuesAsText("name")
                .contains("X-XSRF-TOKEN"));

        List<String> listBookParameters =
                openApi.at("/paths/~1api~1books/get/parameters").findValuesAsText("name");
        assertTrue(listBookParameters.containsAll(
                List.of("title", "author", "isbn", "year", "yearFrom", "yearTo", "category", "page", "size", "sort")));

        List<String> localizationParameters =
                openApi.at("/paths/~1api~1localizations/get/parameters").findValuesAsText("name");
        assertTrue(localizationParameters.containsAll(List.of("messageKey", "language", "page", "size", "sort")));

        List<String> auditLogParameters =
                openApi.at("/paths/~1api~1admin~1audit-logs/get/parameters").findValuesAsText("name");
        assertTrue(
                auditLogParameters.containsAll(List.of("targetType", "action", "actorLogin", "page", "size", "sort")));

        assertEquals("Books", openApi.at("/paths/~1api~1books/get/tags/0").asText());
        assertEquals("Account", openApi.at("/paths/~1api~1account/get/tags/0").asText());
        assertEquals("Session", openApi.at("/paths/~1api~1session/get/tags/0").asText());
        assertEquals(
                "Session",
                openApi.at("/paths/~1api~1session~1logout/post/tags/0").asText());
        assertEquals(
                "Audit Logs",
                openApi.at("/paths/~1api~1admin~1audit-logs/get/tags/0").asText());
        assertEquals(
                "Operator",
                openApi.at("/paths/~1api~1admin~1operator-surface/get/tags/0").asText());
        assertEquals(
                "Admin Users",
                openApi.at("/paths/~1api~1admin~1users/get/tags/0").asText());
        assertEquals(
                "Admin Users",
                openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/tags/0").asText());
        assertEquals(
                "Localizations",
                openApi.at("/paths/~1api~1localizations/get/tags/0").asText());
        assertEquals(
                "Categories",
                openApi.at("/paths/~1api~1categories~1{id}/put/tags/0").asText());
        assertEquals(
                "Categories",
                openApi.at("/paths/~1api~1categories~1{id}/delete/tags/0").asText());
        assertFalse(openApi.at("/paths/~1api~1session/get/responses/200").isMissingNode());
        assertFalse(
                openApi.at("/paths/~1api~1session~1logout/post/responses/204").isMissingNode());
        assertFalse(
                openApi.at("/paths/~1api~1admin~1audit-logs/get/responses/401").isMissingNode());
        assertFalse(
                openApi.at("/paths/~1api~1admin~1audit-logs/get/responses/403").isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1admin~1audit-logs/get/responses/401/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1admin~1audit-logs/get/responses/403/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(openApi.at("/paths/~1api~1admin~1operator-surface/get/responses/401")
                .isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1admin~1operator-surface/get/responses/403")
                .isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1admin~1operator-surface/get/responses/401/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1admin~1operator-surface/get/responses/403/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(openApi.at("/paths/~1api~1admin~1users/get/responses/401").isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1admin~1users/get/responses/403").isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1admin~1users/get/responses/401/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1admin~1users/get/responses/403/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/responses/400")
                .isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/responses/401")
                .isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/responses/403")
                .isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1admin~1users~1{id}~1roles/put/responses/404")
                .isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1admin~1users~1{id}~1roles/put/responses/404/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(openApi.at("/paths/~1api~1categories/post/responses/401").isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1categories/post/responses/403").isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1categories/post/responses/401/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1categories/post/responses/403/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(
                openApi.at("/paths/~1api~1categories~1{id}/put/responses/404").isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1categories~1{id}/delete/responses/404")
                .isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1categories~1{id}/delete/responses/409")
                .isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1categories~1{id}/put/responses/404/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1categories~1{id}/delete/responses/404/content/application~1problem+json/schema/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at(
                                "/paths/~1api~1categories~1{id}/delete/responses/409/content/application~1problem+json/schema/$ref")
                        .asText());
        assertFalse(openApi.at("/components/schemas/Book").isMissingNode());
        assertFalse(openApi.at("/components/schemas/ApiProblemResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/AuditLogResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/AdminUserAccountResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/AdminUserRoleGrantResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/AdminUserRoleUpdateRequest").isMissingNode());
        assertFalse(openApi.at("/components/schemas/BookCreateRequest").isMissingNode());
        assertFalse(openApi.at("/components/schemas/CategoryUpdateRequest").isMissingNode());
        assertFalse(openApi.at("/components/schemas/LocalizationResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/OperatorSurfaceResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/SessionResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/UserAccountResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/AuditLogResponse/properties/details")
                .isMissingNode());
        assertFalse(openApi.at("/components/schemas/SessionLoginProvider").isMissingNode());
        assertFalse(openApi.at("/components/schemas/SessionCsrfContract").isMissingNode());
        assertTrue(openApi.at("/components/schemas/SessionResponse/properties/loginPath")
                .isMissingNode());
        assertEquals(
                "#/components/schemas/SessionLoginProvider",
                openApi.at("/components/schemas/SessionResponse/properties/loginProviders/items/$ref")
                        .asText());
        assertEquals(
                "#/components/schemas/SessionCsrfContract",
                openApi.at("/components/schemas/SessionResponse/properties/csrf/$ref")
                        .asText());
        assertFalse(openApi.at("/components/schemas/SessionCsrfContract/properties/cookieName")
                .isMissingNode());
        assertFalse(openApi.at("/components/schemas/SessionCsrfContract/properties/headerName")
                .isMissingNode());

        assertTimestampProperty(
                openApi,
                "/components/schemas/UserAccountResponse/properties/lastLoginAt",
                "UTC instant of the latest authenticated request.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/UserAccountResponse/properties/createdAt",
                "Creation timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/UserAccountResponse/properties/updatedAt",
                "Last update timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/AdminUserAccountResponse/properties/lastLoginAt",
                "UTC instant of the latest authenticated request.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/AdminUserAccountResponse/properties/createdAt",
                "Creation timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/AdminUserAccountResponse/properties/updatedAt",
                "Last update timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/AdminUserRoleGrantResponse/properties/grantedAt",
                "UTC instant when this role grant was recorded.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/LocalizationResponse/properties/createdAt",
                "Creation timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/LocalizationResponse/properties/updatedAt",
                "Last update timestamp as a UTC instant.");
        assertTimestampProperty(
                openApi,
                "/components/schemas/AuditLogResponse/properties/createdAt",
                "Creation timestamp as a UTC instant.");
    }

    private JsonNode fetchOpenApiJson() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs");
        assertEquals(200, response.statusCode());
        return OBJECT_MAPPER.readTree(response.body());
    }

    private void assertTimestampProperty(JsonNode openApi, String propertyPointer, String description) {
        assertEquals(description, openApi.at(propertyPointer + "/description").asText());
        assertEquals("string", openApi.at(propertyPointer + "/type").asText());
        assertEquals("date-time", openApi.at(propertyPointer + "/format").asText());
    }
}
