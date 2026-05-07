package team.jit.technicalinterviewdemo.manualregression.harness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** HTTP response returned by the manual-regression HTTP harness. */
public final class HarnessResponse {

    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final String body;
    private HarnessJsonPath jsonPath;

    HarnessResponse(int statusCode, Map<String, List<String>> headers, String body) {
        this.statusCode = statusCode;
        this.headers = copy(headers);
        this.body = body == null ? "" : body;
    }

    public int statusCode() {
        return statusCode;
    }

    public String asString() {
        return body;
    }

    public HarnessJsonPath jsonPath() {
        if (jsonPath == null) {
            jsonPath = new HarnessJsonPath(body);
        }
        return jsonPath;
    }

    Map<String, List<String>> headers() {
        return copy(headers);
    }

    private static Map<String, List<String>> copy(Map<String, List<String>> source) {
        Map<String, List<String>> copy = new LinkedHashMap<>();
        source.forEach((name, values) -> copy.put(name, List.copyOf(values)));
        return copy;
    }
}
