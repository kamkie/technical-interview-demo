package team.jit.technicalinterviewdemo.technical.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcIntegrationSpringBootTest
@ActiveProfiles(
        value = {"test", "oauth", "fake-oauth"},
        inheritProfiles = false)
@TestPropertySource(
        properties = {
            "app.security.oauth.providers.smoke.token-uri=http://localhost/test-support/oauth2/token",
            "app.security.oauth.providers.smoke.user-info-uri=http://localhost/test-support/oauth2/userinfo"
        })
class FakeOAuthProviderControllerIntegrationTests extends AbstractMockMvcIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void fakeProviderExchangesAuthorizationCodeForSmokeUserInfo() throws Exception {
        MvcResult authorizationResult = mockMvc.perform(get("/test-support/oauth2/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "technical-interview-demo-smoke")
                        .queryParam("redirect_uri", "http://localhost/api/session/login/oauth2/code/smoke")
                        .queryParam("scope", "profile email")
                        .queryParam("state", "opaque-state="))
                .andExpect(status().isFound())
                .andExpect(header().string(
                                HttpHeaders.LOCATION, org.hamcrest.Matchers.containsString("state=opaque-state=")))
                .andReturn();

        String callbackLocation = authorizationResult.getResponse().getHeader(HttpHeaders.LOCATION);
        String authorizationCode = UriComponentsBuilder.fromUriString(callbackLocation)
                .build()
                .getQueryParams()
                .getFirst("code");

        MvcResult tokenResult = mockMvc.perform(post("/test-support/oauth2/token")
                        .header(HttpHeaders.AUTHORIZATION, basicAuthorization())
                        .contentType("application/x-www-form-urlencoded")
                        .content("grant_type=authorization_code&code=" + authorizationCode))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode token = OBJECT_MAPPER.readTree(tokenResult.getResponse().getContentAsString());

        MvcResult userInfoResult = mockMvc.perform(get("/test-support/oauth2/userinfo")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token.path("access_token").asText()))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode userInfo = OBJECT_MAPPER.readTree(userInfoResult.getResponse().getContentAsString());

        assertThat(token.path("token_type").asText()).isEqualTo("Bearer");
        assertThat(userInfo.path("login").asText()).isEqualTo("smoke-user");
        assertThat(userInfo.path("name").asText()).isEqualTo("Smoke Test User");
        assertThat(userInfo.path("email").asText()).isEqualTo("smoke-user@example.test");
    }

    @Test
    void fakeProviderRejectsRedirectsOutsideCurrentHost() throws Exception {
        mockMvc.perform(get("/test-support/oauth2/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "technical-interview-demo-smoke")
                        .queryParam("redirect_uri", "https://evil.example/api/session/login/oauth2/code/smoke"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fakeProviderRejectsRedirectsOutsideCurrentPort() throws Exception {
        mockMvc.perform(get("/test-support/oauth2/authorize")
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "technical-interview-demo-smoke")
                        .queryParam("redirect_uri", "http://localhost:8081/api/session/login/oauth2/code/smoke"))
                .andExpect(status().isBadRequest());
    }

    private String basicAuthorization() {
        String credentials = "technical-interview-demo-smoke:technical-interview-demo-smoke-secret";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
