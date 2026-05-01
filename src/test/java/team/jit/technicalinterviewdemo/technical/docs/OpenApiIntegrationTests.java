package team.jit.technicalinterviewdemo.technical.docs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.testing.AbstractRandomPortIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RandomPortIntegrationSpringBootTest;

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
                "Machine-readable contract for the demo application's stable 1.x supported HTTP surface,"
                        + " including the public overview and documentation endpoints plus the secured business"
                        + " API operations. Deployment-scoped technical endpoints such as"
                        + " /actuator/prometheus are documented separately.",
                openApi.at("/info/description").asText()
        );
        assertFalse(openApi.at("/paths/~1/get").isMissingNode());
        assertEquals("apiKey", openApi.at("/components/securitySchemes/sessionCookie/type").asText());
        assertEquals("cookie", openApi.at("/components/securitySchemes/sessionCookie/in").asText());
        assertEquals(
                "technical-interview-demo-session",
                openApi.at("/components/securitySchemes/sessionCookie/name").asText()
        );
        assertEquals(
                "Authenticated browser session cookie used by protected operations. It is established through"
                        + " GET /oauth2/authorization/github when the optional oauth profile is active.",
                openApi.at("/components/securitySchemes/sessionCookie/description").asText()
        );

        JsonNode createBookSecurity = openApi.at("/paths/~1api~1books/post/security");
        assertFalse(createBookSecurity.isMissingNode());
        assertEquals("sessionCookie", createBookSecurity.get(0).fieldNames().next());

        JsonNode accountSecurity = openApi.at("/paths/~1api~1account/get/security");
        assertFalse(accountSecurity.isMissingNode());
        assertEquals("sessionCookie", accountSecurity.get(0).fieldNames().next());

        List<String> listBookParameters = openApi.at("/paths/~1api~1books/get/parameters")
                .findValuesAsText("name");
        assertTrue(listBookParameters.containsAll(List.of(
                "title",
                "author",
                "isbn",
                "year",
                "yearFrom",
                "yearTo",
                "category",
                "page",
                "size",
                "sort"
        )));

        List<String> localizationParameters = openApi.at("/paths/~1api~1localizations/get/parameters")
                .findValuesAsText("name");
        assertTrue(localizationParameters.containsAll(List.of("messageKey", "language", "page", "size", "sort")));

        assertEquals("Books", openApi.at("/paths/~1api~1books/get/tags/0").asText());
        assertEquals("Account", openApi.at("/paths/~1api~1account/get/tags/0").asText());
        assertEquals("Localizations", openApi.at("/paths/~1api~1localizations/get/tags/0").asText());
        assertFalse(openApi.at("/paths/~1api~1categories/post/responses/401").isMissingNode());
        assertFalse(openApi.at("/paths/~1api~1categories/post/responses/403").isMissingNode());
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1categories/post/responses/401/content/application~1problem+json/schema/$ref").asText()
        );
        assertEquals(
                "#/components/schemas/ApiProblemResponse",
                openApi.at("/paths/~1api~1categories/post/responses/403/content/application~1problem+json/schema/$ref").asText()
        );
        assertFalse(openApi.at("/components/schemas/Book").isMissingNode());
        assertFalse(openApi.at("/components/schemas/ApiProblemResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/BookCreateRequest").isMissingNode());
        assertFalse(openApi.at("/components/schemas/LocalizationResponse").isMissingNode());
        assertFalse(openApi.at("/components/schemas/UserAccountResponse").isMissingNode());
    }

    private JsonNode fetchOpenApiJson() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/v3/api-docs");
        assertEquals(200, response.statusCode());
        return OBJECT_MAPPER.readTree(response.body());
    }
}
