package team.jit.technicalinterviewdemo.technical.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.user.UserAccount;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.testing.TestcontainersTest;

import java.io.IOException;
import java.net.CookieManager;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@TestcontainersTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(
        value = {"test", "oauth", "fake-oauth"},
        inheritProfiles = false)
@ContextConfiguration(initializers = FakeOAuthLoginFlowIntegrationTests.PortInitializer.class)
class FakeOAuthLoginFlowIntegrationTests {

    private static final int SERVER_PORT = availablePort();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(new CookieManager())
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void clearState() {
        jdbcTemplate.update("DELETE FROM SPRING_SESSION_ATTRIBUTES");
        jdbcTemplate.update("DELETE FROM SPRING_SESSION");
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void fakeOauthProviderCompletesBrowserLoginFlow() throws Exception {
        HttpResponse<String> bootstrapResponse = get("/api/session");
        JsonNode bootstrap = OBJECT_MAPPER.readTree(bootstrapResponse.body());

        assertThat(bootstrapResponse.statusCode()).isEqualTo(200);
        assertThat(bootstrap.path("authenticated").asBoolean()).isFalse();
        JsonNode smokeProvider = loginProvider(bootstrap, "smoke");
        assertThat(smokeProvider.path("clientName").asText()).isEqualTo("Smoke OAuth");
        assertThat(smokeProvider.path("authorizationPath").asText())
                .isEqualTo("/api/session/oauth2/authorization/smoke");

        HttpResponse<String> authorizationStartResponse = get("/api/session/oauth2/authorization/smoke");
        URI providerAuthorizeUri = redirectLocation(authorizationStartResponse);
        assertThat(providerAuthorizeUri.getPath()).isEqualTo("/test-support/oauth2/authorize");

        HttpResponse<String> providerAuthorizeResponse = get(providerAuthorizeUri);
        URI callbackUri = redirectLocation(providerAuthorizeResponse);
        assertThat(callbackUri.getPath()).isEqualTo("/api/session/login/oauth2/code/smoke");
        assertThat(UriComponentsBuilder.fromUri(callbackUri)
                        .build()
                        .getQueryParams()
                        .getFirst("code"))
                .isNotBlank();

        HttpResponse<String> callbackResponse = get(callbackUri);
        URI finalRedirect = redirectLocation(callbackResponse);
        assertThat(finalRedirect.getPath()).isEqualTo("/");

        HttpResponse<String> accountResponse = get("/api/account");
        JsonNode account = OBJECT_MAPPER.readTree(accountResponse.body());

        assertThat(accountResponse.statusCode()).isEqualTo(200);
        assertThat(account.path("provider").asText()).isEqualTo("smoke");
        assertThat(account.path("login").asText()).isEqualTo("smoke-user");
        assertThat(account.path("displayName").asText()).isEqualTo("Smoke Test User");
        assertThat(account.path("email").asText()).isEqualTo("smoke-user@example.test");

        HttpResponse<String> authenticatedSessionResponse = get("/api/session");
        JsonNode authenticatedSession = OBJECT_MAPPER.readTree(authenticatedSessionResponse.body());

        assertThat(authenticatedSession.path("authenticated").asBoolean()).isTrue();
        assertThat(auditLogRepository.findAll()).hasSize(1);
    }

    @Test
    void blockedAccountLosesActiveSessionAndCannotSignInAgain() throws Exception {
        completeLoginFlow();
        HttpResponse<String> syncedAccountResponse = get("/api/account");
        assertThat(syncedAccountResponse.statusCode()).isEqualTo(200);

        UserAccount smokeUser = userAccountRepository
                .findByProviderAndExternalLogin("smoke", "smoke-user")
                .orElseThrow();
        smokeUser.block(null, "Blocked mid-session for testing.");
        userAccountRepository.saveAndFlush(smokeUser);
        auditLogRepository.deleteAll();

        HttpResponse<String> rejectedResponse = get("/api/account");
        assertThat(rejectedResponse.statusCode()).isEqualTo(401);
        assertThat(rejectedResponse.headers().firstValue("Content-Type").orElse(""))
                .contains("application/problem+json");
        assertThat(OBJECT_MAPPER.readTree(rejectedResponse.body()).path("title").asText())
                .isEqualTo("Unauthorized");

        HttpResponse<String> sessionAfterRejection = get("/api/session");
        assertThat(OBJECT_MAPPER
                        .readTree(sessionAfterRejection.body())
                        .path("authenticated")
                        .asBoolean())
                .isFalse();
        assertThat(auditLogRepository.findAll())
                .singleElement()
                .satisfies(auditLog -> assertThat(auditLog.getAction()).isEqualTo(AuditAction.SESSION_REJECTION));
        Instant lastLoginBeforeRetry = userAccountRepository
                .findByProviderAndExternalLogin("smoke", "smoke-user")
                .orElseThrow()
                .getLastLoginAt();
        auditLogRepository.deleteAll();

        URI retryRedirect = completeLoginFlowRedirect();
        assertThat(retryRedirect.getPath()).isEqualTo("/");
        assertThat(retryRedirect.getQuery()).isEqualTo("login=failed");

        HttpResponse<String> sessionAfterRetry = get("/api/session");
        assertThat(OBJECT_MAPPER
                        .readTree(sessionAfterRetry.body())
                        .path("authenticated")
                        .asBoolean())
                .isFalse();
        assertThat(auditLogRepository.findAll()).hasSize(1);
        AuditLog loginFailure = auditLogRepository.findAll().getFirst();
        assertThat(loginFailure.getAction()).isEqualTo(AuditAction.LOGIN_FAILURE);
        assertThat(loginFailure.getDetails())
                .containsEntry("failureReason", "account_blocked")
                .containsEntry("provider", "smoke")
                .containsEntry("login", "smoke-user");
        assertThat(userAccountRepository
                        .findByProviderAndExternalLogin("smoke", "smoke-user")
                        .orElseThrow()
                        .getLastLoginAt())
                .isEqualTo(lastLoginBeforeRetry);
    }

    private void completeLoginFlow() throws IOException, InterruptedException {
        URI finalRedirect = completeLoginFlowRedirect();
        assertThat(finalRedirect.getPath()).isEqualTo("/");
        assertThat(finalRedirect.getQuery()).isNull();
    }

    private URI completeLoginFlowRedirect() throws IOException, InterruptedException {
        HttpResponse<String> authorizationStartResponse = get("/api/session/oauth2/authorization/smoke");
        URI providerAuthorizeUri = redirectLocation(authorizationStartResponse);
        HttpResponse<String> providerAuthorizeResponse = get(providerAuthorizeUri);
        URI callbackUri = redirectLocation(providerAuthorizeResponse);
        HttpResponse<String> callbackResponse = get(callbackUri);
        return redirectLocation(callbackResponse);
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return get(URI.create("http://127.0.0.1:" + SERVER_PORT + path));
    }

    private HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        return httpClient.send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI redirectLocation(HttpResponse<String> response) {
        assertThat(response.statusCode()).isBetween(300, 399);
        return URI.create(response.headers().firstValue(HttpHeaders.LOCATION).orElseThrow());
    }

    private JsonNode loginProvider(JsonNode session, String registrationId) {
        return StreamSupport.stream(session.path("loginProviders").spliterator(), false)
                .filter(provider ->
                        registrationId.equals(provider.path("registrationId").asText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected login provider: " + registrationId));
    }

    private static int availablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not reserve a port for fake OAuth login tests.", exception);
        }
    }

    static class PortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String baseUrl = "http://127.0.0.1:" + SERVER_PORT;
            TestPropertyValues.of(
                            "server.port=" + SERVER_PORT,
                            "app.security.oauth.providers.smoke.authorization-uri=" + baseUrl
                                    + "/test-support/oauth2/authorize",
                            "app.security.oauth.providers.smoke.token-uri=" + baseUrl + "/test-support/oauth2/token",
                            "app.security.oauth.providers.smoke.user-info-uri=" + baseUrl
                                    + "/test-support/oauth2/userinfo")
                    .applyTo(applicationContext);
        }
    }
}
