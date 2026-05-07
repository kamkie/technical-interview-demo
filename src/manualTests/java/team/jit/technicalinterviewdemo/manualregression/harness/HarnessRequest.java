package team.jit.technicalinterviewdemo.manualregression.harness;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Mutable request specification used by the manual-regression HTTP harness. */
public final class HarnessRequest {

    private static final String JSON_MEDIA_TYPE = "application/json";

    private final String baseUrl;
    private final Map<String, List<String>> headers = new LinkedHashMap<>();
    private String body;

    HarnessRequest(String baseUrl) {
        this.baseUrl = baseUrl;
        accept(JSON_MEDIA_TYPE);
    }

    public HarnessRequest accept(String mediaType) {
        return setHeader("Accept", mediaType);
    }

    public HarnessRequest contentType(String mediaType) {
        return setHeader("Content-Type", mediaType);
    }

    public HarnessRequest header(String name, String value) {
        headers.computeIfAbsent(name, ignored -> new ArrayList<>()).add(value);
        return this;
    }

    public HarnessRequest cookie(String name, String value) {
        return header("Cookie", name + "=" + value);
    }

    private HarnessRequest setHeader(String name, String value) {
        headers.put(name, new ArrayList<>(List.of(value)));
        return this;
    }

    public HarnessRequest body(String body) {
        this.body = body;
        return this;
    }

    Optional<String> body() {
        return Optional.ofNullable(body);
    }

    Map<String, List<String>> headers() {
        Map<String, List<String>> copy = new LinkedHashMap<>();
        headers.forEach((name, values) -> copy.put(name, List.copyOf(values)));
        return copy;
    }

    URI resolve(String pathOrUrl) {
        URI candidate = URI.create(pathOrUrl);
        if (candidate.isAbsolute()) {
            return candidate;
        }
        return URI.create(baseUrl).resolve(pathOrUrl);
    }

    HttpRequest toHttpRequest(String method, String pathOrUrl, Duration timeout) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(resolve(pathOrUrl)).timeout(timeout);
        headers.forEach((name, values) -> values.forEach(value -> builder.header(name, value)));
        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);
        return builder.method(method, publisher).build();
    }
}
