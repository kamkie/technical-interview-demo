package team.jit.technicalinterviewdemo.technical.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationService;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiSecurityErrorHandlerTests {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().findAndAddModules().build();

    @Mock
    private LocalizationService localizationService;

    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private ApiAccessDeniedHandler apiAccessDeniedHandler;

    @BeforeEach
    void setUp() {
        ApiProblemFactory apiProblemFactory = new ApiProblemFactory(localizationService);
        apiAuthenticationEntryPoint = new ApiAuthenticationEntryPoint(apiProblemFactory);
        apiAccessDeniedHandler = new ApiAccessDeniedHandler(apiProblemFactory);
    }

    @Test
    void authenticationEntryPointWritesLocalizedUnauthorizedProblem() throws Exception {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.unauthorized")))
                .thenReturn(localizedMessage("error.request.unauthorized", "You must authenticate first.", "en"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/categories");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAuthenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException("Authentication required")
        );

        JsonNode body = JSON_MAPPER.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(body.get("title").asText()).isEqualTo("Unauthorized");
        assertThat(body.get("status").asInt()).isEqualTo(401);
        assertThat(body.get("detail").asText()).isEqualTo("Authentication is required to access this resource.");
        assertThat(body.get("messageKey").asText()).isEqualTo("error.request.unauthorized");
        assertThat(body.get("message").asText()).isEqualTo("You must authenticate first.");
        assertThat(body.get("language").asText()).isEqualTo("en");
    }

    @Test
    void accessDeniedHandlerPreservesExplicitDetailWhenPresent() throws Exception {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.forbidden")))
                .thenReturn(localizedMessage("error.request.forbidden", "Forbidden message.", "pl"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/categories");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAccessDeniedHandler.handle(
                request,
                response,
                new AccessDeniedException("Category management requires the ADMIN role.")
        );

        JsonNode body = JSON_MAPPER.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(body.get("detail").asText()).isEqualTo("Category management requires the ADMIN role.");
        assertThat(body.get("messageKey").asText()).isEqualTo("error.request.forbidden");
        assertThat(body.get("message").asText()).isEqualTo("Forbidden message.");
        assertThat(body.get("language").asText()).isEqualTo("pl");
    }

    @Test
    void accessDeniedHandlerUsesFallbackDetailWhenMessageIsBlank() throws Exception {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.forbidden")))
                .thenReturn(localizedMessage("error.request.forbidden", "Forbidden message.", "en"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/categories");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAccessDeniedHandler.handle(request, response, new AccessDeniedException(" "));

        JsonNode body = JSON_MAPPER.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(body.get("title").asText()).isEqualTo("Forbidden");
        assertThat(body.get("status").asInt()).isEqualTo(403);
        assertThat(body.get("detail").asText()).isEqualTo("Access is denied.");
        assertThat(body.get("messageKey").asText()).isEqualTo("error.request.forbidden");
        assertThat(body.get("language").asText()).isEqualTo("en");
    }

    @Test
    void accessDeniedHandlerMapsCsrfFailuresToDedicatedLocalizedProblem() throws Exception {
        when(localizationService.findByMessageKeyForCurrentLanguageWithFallback(eq("error.request.csrf_invalid")))
                .thenReturn(localizedMessage("error.request.csrf_invalid", "CSRF message.", "fr"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/books");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAccessDeniedHandler.handle(request, response, new MissingCsrfTokenException("missing"));

        JsonNode body = JSON_MAPPER.readTree(response.getContentAsString());
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(body.get("title").asText()).isEqualTo("Invalid CSRF Token");
        assertThat(body.get("status").asInt()).isEqualTo(403);
        assertThat(body.get("detail").asText()).isEqualTo("A valid CSRF token is required to perform this operation.");
        assertThat(body.get("messageKey").asText()).isEqualTo("error.request.csrf_invalid");
        assertThat(body.get("message").asText()).isEqualTo("CSRF message.");
        assertThat(body.get("language").asText()).isEqualTo("fr");
    }

    private Localization localizedMessage(String messageKey, String messageText, String language) {
        return new Localization(messageKey, language, messageText, null);
    }
}
