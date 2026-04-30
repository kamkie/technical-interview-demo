package team.jit.technicalinterviewdemo.technical.logging;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class SensitiveDataSanitizer {

    public static final String REDACTED = "<redacted>";

    private static final String[] SENSITIVE_TOKENS = {
            "password",
            "passwd",
            "pwd",
            "secret",
            "token",
            "authorization",
            "credential",
            "cookie",
            "session",
            "apikey",
            "accesskey",
            "privatekey",
            "clientsecret",
            "bearer",
            "refreshtoken"
    };

    private SensitiveDataSanitizer() {
    }

    public static boolean isSensitiveName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String normalized = normalize(name);
        for (String token : SENSITIVE_TOKENS) {
            if (normalized.contains(normalize(token))) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Object> sanitizeParameters(Map<String, String[]> parameterMap) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        parameterMap.forEach((name, values) -> sanitized.put(
                name,
                isSensitiveName(name) ? REDACTED : sanitizeValues(values)
        ));
        return sanitized;
    }

    private static Object sanitizeValues(String[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }
        return Arrays.asList(values);
    }

    private static String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
    }
}
