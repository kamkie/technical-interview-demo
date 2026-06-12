package team.jit.technicalinterviewdemo.technical.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import team.jit.technicalinterviewdemo.testing.AbstractRandomPortIntegrationTest;
import team.jit.technicalinterviewdemo.testing.RandomPortIntegrationSpringBootTest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Failures that escape the servlet filter chain bypass {@link ApiExceptionHandler} and reach the
 * container error dispatch. These tests pin that dispatch to the localized problem-details
 * contract instead of the Spring Boot default error body.
 */
@RandomPortIntegrationSpringBootTest
@Import(ErrorDispatchProblemDetailsIntegrationTests.SimulatedFilterFailureConfiguration.class)
class ErrorDispatchProblemDetailsIntegrationTests extends AbstractRandomPortIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FAILURE_HEADER = "X-Simulated-Filter-Failure";

    @Test
    void escapedFilterFailureReturnsLocalizedProblemDetails() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/books"))
                .header(FAILURE_HEADER, "true")
                .GET()
                .build());

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.headers().firstValue("Content-Type").orElse("")).startsWith("application/problem+json");
        JsonNode body = OBJECT_MAPPER.readTree(response.body());
        assertThat(body.path("title").asText()).isEqualTo("Internal Server Error");
        assertThat(body.path("status").asInt()).isEqualTo(500);
        assertThat(body.path("detail").asText()).isEqualTo("An unexpected error occurred.");
        assertThat(body.path("messageKey").asText()).isEqualTo("error.server.internal");
        assertThat(body.path("message").asText()).isEqualTo("An unexpected error occurred.");
        assertThat(body.path("language").asText()).isEqualTo("en");
        assertThat(body.has("trace")).isFalse();
        assertThat(body.path("detail").asText()).doesNotContain("Simulated");
    }

    @Test
    void escapedFilterFailureHonorsLanguageOverride() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/books?lang=pl"))
                .header(FAILURE_HEADER, "true")
                .GET()
                .build());

        assertThat(response.statusCode()).isEqualTo(500);
        JsonNode body = OBJECT_MAPPER.readTree(response.body());
        assertThat(body.path("messageKey").asText()).isEqualTo("error.server.internal");
        assertThat(body.path("message").asText()).isEqualTo("Wystąpił nieoczekiwany błąd.");
        assertThat(body.path("language").asText()).isEqualTo("pl");
    }

    @Test
    void directErrorDispatchWithoutAttributesDefaultsToServerProblem() throws Exception {
        HttpResponse<String> response = get("/error");

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.headers().firstValue("Content-Type").orElse("")).startsWith("application/problem+json");
        JsonNode body = OBJECT_MAPPER.readTree(response.body());
        assertThat(body.path("title").asText()).isEqualTo("Internal Server Error");
        assertThat(body.path("messageKey").asText()).isEqualTo("error.server.internal");
    }

    @TestConfiguration
    static class SimulatedFilterFailureConfiguration {

        @Bean
        FilterRegistrationBean<Filter> simulatedFilterFailureRegistration() {
            FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>((request, response, chain) -> {
                if (((HttpServletRequest) request).getHeader(FAILURE_HEADER) != null) {
                    throw new IllegalStateException("Simulated filter-chain failure.");
                }
                chain.doFilter(request, response);
            });
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return registration;
        }
    }
}
