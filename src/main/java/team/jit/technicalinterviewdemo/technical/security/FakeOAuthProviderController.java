package team.jit.technicalinterviewdemo.technical.security;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Hidden
@RestController
@Profile("fake-oauth & !prod")
@RequestMapping("/test-support/oauth2")
@RequiredArgsConstructor
public class FakeOAuthProviderController {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BASIC_PREFIX = "Basic ";

    private final FakeOAuthProviderProperties properties;
    private final FakeOAuthAuthorizationStore authorizationStore;

    @GetMapping("/authorize")
    ResponseEntity<Void> authorize(
            @RequestParam("response_type") String responseType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {
        if (!"code".equals(responseType)
                || !hasText(clientId)
                || !constantTimeEquals(properties.getClientId(), clientId)
                || !isAllowedRedirectUri(redirectUri, request)) {
            return ResponseEntity.badRequest().build();
        }

        FakeOAuthAuthorizationStore.AuthorizedUser user = new FakeOAuthAuthorizationStore.AuthorizedUser(
                properties.getLogin(), properties.getDisplayName(), properties.getEmail(), normalizedScope(scope));
        String authorizationCode = authorizationStore.issueGrantCode(user);
        UriComponentsBuilder redirectBuilder =
                UriComponentsBuilder.fromUriString(redirectUri).queryParam("code", authorizationCode);
        if (hasText(state)) {
            redirectBuilder.queryParam("state", state);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectBuilder.build().toUri())
                .build();
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, Object>> token(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestParam MultiValueMap<String, String> form) {
        if (!hasValidClientCredentials(authorizationHeader, form)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.<String, Object>of("error", "invalid_client"));
        }
        String grantType = form.getFirst("grant_type");
        String authorizationCode = form.getFirst("code");
        if (!"authorization_code".equals(grantType) || !hasText(authorizationCode)) {
            return ResponseEntity.badRequest().body(Map.<String, Object>of("error", "unsupported_grant_type"));
        }

        return authorizationStore
                .consumeGrantCode(authorizationCode)
                .map(user -> {
                    String accessToken = authorizationStore.createAccessToken(user);
                    return ResponseEntity.ok(Map.<String, Object>of(
                            "access_token",
                            accessToken,
                            "token_type",
                            "Bearer",
                            "expires_in",
                            300,
                            "scope",
                            String.join(" ", user.scope())));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.<String, Object>of("error", "invalid_grant")));
    }

    @GetMapping("/userinfo")
    ResponseEntity<Map<String, String>> userInfo(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        Optional<String> accessToken = bearerToken(authorizationHeader);
        if (accessToken.isEmpty()) {
            return invalidToken();
        }

        return authorizationStore
                .findAccessToken(accessToken.get())
                .map(user -> ResponseEntity.ok(Map.of(
                        "login", user.login(),
                        "name", user.displayName(),
                        "email", user.email())))
                .orElseGet(this::invalidToken);
    }

    private boolean hasValidClientCredentials(String authorizationHeader, MultiValueMap<String, String> form) {
        Optional<ClientCredentials> basicCredentials = basicCredentials(authorizationHeader);
        if (basicCredentials.isPresent()) {
            return clientCredentialsMatch(basicCredentials.get());
        }
        return clientCredentialsMatch(
                new ClientCredentials(form.getFirst("client_id"), form.getFirst("client_secret")));
    }

    private Optional<ClientCredentials> basicCredentials(String authorizationHeader) {
        if (!hasText(authorizationHeader) || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            return Optional.empty();
        }
        try {
            String decoded = new String(
                    Base64.getDecoder().decode(authorizationHeader.substring(BASIC_PREFIX.length())),
                    StandardCharsets.UTF_8);
            int separatorIndex = decoded.indexOf(':');
            if (separatorIndex < 0) {
                return Optional.empty();
            }
            return Optional.of(
                    new ClientCredentials(decoded.substring(0, separatorIndex), decoded.substring(separatorIndex + 1)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private boolean clientCredentialsMatch(ClientCredentials credentials) {
        return constantTimeEquals(properties.getClientId(), credentials.clientId())
                && constantTimeEquals(properties.getClientSecret(), credentials.clientSecret());
    }

    private Optional<String> bearerToken(String authorizationHeader) {
        if (!hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return hasText(token) ? Optional.of(token) : Optional.empty();
    }

    private ResponseEntity<Map<String, String>> invalidToken() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .body(Map.of("error", "invalid_token"));
    }

    private boolean isAllowedRedirectUri(String redirectUri, HttpServletRequest request) {
        if (!hasText(redirectUri)) {
            return false;
        }
        URI uri;
        try {
            uri = URI.create(redirectUri);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        String scheme = uri.getScheme();
        return uri.isAbsolute()
                && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                && request.getServerName().equalsIgnoreCase(uri.getHost())
                && effectivePort(uri) == request.getServerPort()
                && uri.getPath().startsWith(SecuritySettingsProperties.OAuth.CALLBACK_BASE_URI + "/");
    }

    private int effectivePort(URI uri) {
        int port = uri.getPort();
        if (port >= 0) {
            return port;
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private Set<String> normalizedScope(String scope) {
        if (!hasText(scope)) {
            return Set.of();
        }
        return Arrays.stream(scope.split(" "))
                .map(String::trim)
                .filter(FakeOAuthProviderController::hasText)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private record ClientCredentials(String clientId, String clientSecret) {}
}
