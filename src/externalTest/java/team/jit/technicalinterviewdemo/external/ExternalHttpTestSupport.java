package team.jit.technicalinterviewdemo.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

abstract class ExternalHttpTestSupport {

    protected static final Duration HTTP_TIMEOUT = Duration.ofSeconds(15);
    protected static final String SESSION_COOKIE_NAME = "technical-interview-demo-session";
    protected static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).build();

    protected HttpResponse<String> get(String path, String accept) throws IOException, InterruptedException {
        return get(path, accept, Map.of());
    }

    protected HttpResponse<String> get(String path, String accept, Map<String, String> headers)
            throws IOException, InterruptedException {
        return send("GET", path, accept, headers, Map.of(), null);
    }

    protected URI uriFor(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/': " + path);
        }
        return URI.create(baseUrl() + path);
    }

    protected HttpResponse<String> getWithSession(String path, String accept, String sessionId)
            throws IOException, InterruptedException {
        return getWithCookies(path, accept, Map.of(SESSION_COOKIE_NAME, encodedSessionCookieValue(sessionId)));
    }

    protected HttpResponse<String> getWithCookies(String path, String accept, Map<String, String> cookies)
            throws IOException, InterruptedException {
        return send("GET", path, accept, Map.of(), cookies, null);
    }

    protected HttpResponse<String> putJsonWithCookies(
            String path, String accept, Map<String, String> headers, Map<String, String> cookies, String jsonBody)
            throws IOException, InterruptedException {
        return send("PUT", path, accept, headers, cookies, jsonBody);
    }

    protected String requiredHeader(HttpResponse<String> response, String headerName) {
        return response.headers()
                .firstValue(headerName)
                .orElseThrow(() -> new AssertionError("Expected header '" + headerName + "' to be present."));
    }

    protected String requiredCookie(HttpResponse<String> response, String cookieName) {
        return response.headers().allValues("set-cookie").stream()
                .map(String::trim)
                .filter(headerValue -> headerValue.startsWith(cookieName + "="))
                .map(headerValue -> {
                    String cookieValue = headerValue.substring((cookieName + "=").length());
                    int delimiterIndex = cookieValue.indexOf(';');
                    return delimiterIndex >= 0 ? cookieValue.substring(0, delimiterIndex) : cookieValue;
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected cookie '" + cookieName + "' to be present."));
    }

    protected boolean bodyContainsRegex(HttpResponse<String> response, String regex) {
        return response.body().replace("\n", "").matches(".*" + regex + ".*");
    }

    protected JsonNode jsonBody(HttpResponse<String> response) {
        try {
            return OBJECT_MAPPER.readTree(response.body());
        } catch (IOException exception) {
            throw new AssertionError("Expected a JSON response body.", exception);
        }
    }

    protected static String encodedSessionCookieValue(String sessionId) {
        return Base64.getEncoder().encodeToString(sessionId.getBytes(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> send(
            String method,
            String path,
            String accept,
            Map<String, String> headers,
            Map<String, String> cookies,
            String body)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder =
                HttpRequest.newBuilder(uriFor(path)).timeout(HTTP_TIMEOUT).header("Accept", accept);
        headers.forEach(builder::header);
        if (!cookies.isEmpty()) {
            builder.header("Cookie", cookieHeader(cookies));
        }

        HttpRequest request =
                switch (method) {
                    case "GET" -> builder.GET().build();
                    case "PUT" ->
                        builder.header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(body == null ? "" : body))
                                .build();
                    default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
                };
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String cookieHeader(Map<String, String> cookies) {
        StringBuilder headerValue = new StringBuilder();
        cookies.forEach((name, value) -> {
            if (headerValue.length() > 0) {
                headerValue.append("; ");
            }
            headerValue.append(name).append("=").append(value);
        });
        return headerValue.toString();
    }

    private String baseUrl() {
        String configured = firstNonBlank(
                System.getProperty("external.baseUrl"),
                System.getProperty("externalBaseUrl"),
                System.getProperty("baseUrl"),
                System.getenv("EXTERNAL_BASE_URL"),
                System.getenv("BASE_URL"));
        if (configured == null) {
            throw new IllegalStateException("External base URL is not configured. Set one of: "
                    + "-Dexternal.baseUrl, -DexternalBaseUrl, -DbaseUrl, EXTERNAL_BASE_URL, BASE_URL");
        }
        String trimmed = configured.trim();
        if (trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    protected static String firstNonBlank(String... values) {
        for (String value : values) {
            if (Optional.ofNullable(value)
                    .map(String::trim)
                    .filter(v -> !v.isEmpty())
                    .isPresent()) {
                return value;
            }
        }
        return null;
    }
}
