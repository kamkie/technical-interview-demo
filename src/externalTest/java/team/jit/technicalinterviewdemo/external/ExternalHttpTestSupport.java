package team.jit.technicalinterviewdemo.external;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

abstract class ExternalHttpTestSupport {

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT)
            .build();

    protected HttpResponse<String> get(String path, String accept) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .timeout(HTTP_TIMEOUT)
                .header("Accept", accept)
                .GET()
                .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected String requiredHeader(HttpResponse<String> response, String headerName) {
        return response.headers().firstValue(headerName)
                .orElseThrow(() -> new AssertionError("Expected header '" + headerName + "' to be present."));
    }

    protected boolean bodyContainsRegex(HttpResponse<String> response, String regex) {
        return response.body().replace("\n", "").matches(".*" + regex + ".*");
    }

    private URI uri(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/': " + path);
        }
        return URI.create(baseUrl() + path);
    }

    private String baseUrl() {
        String configured = firstNonBlank(
                System.getProperty("external.baseUrl"),
                System.getProperty("externalBaseUrl"),
                System.getProperty("baseUrl"),
                System.getenv("EXTERNAL_BASE_URL"),
                System.getenv("BASE_URL")
        );
        if (configured == null) {
            throw new IllegalStateException(
                    "External base URL is not configured. Set one of: "
                            + "-Dexternal.baseUrl, -DexternalBaseUrl, -DbaseUrl, EXTERNAL_BASE_URL, BASE_URL"
            );
        }
        String trimmed = configured.trim();
        if (trimmed.endsWith("/")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (Optional.ofNullable(value).map(String::trim).filter(v -> !v.isEmpty()).isPresent()) {
                return value;
            }
        }
        return null;
    }
}
