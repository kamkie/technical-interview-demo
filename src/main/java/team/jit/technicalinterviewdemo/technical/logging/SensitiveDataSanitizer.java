package team.jit.technicalinterviewdemo.technical.logging;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SensitiveDataSanitizer {

    public static final String REDACTED = "<redacted>";

    private static final String[] SENSITIVE_TOKENS = {"password", "passwd", "pwd", "secret", "token", "authorization", "credential", "cookie", "session", "apikey", "accesskey", "privatekey", "clientsecret", "bearer", "refreshtoken"
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
            sanitizeForLog(name), isSensitiveName(name) ? REDACTED : sanitizeValues(values)
        ));
        return sanitized;
    }

    public static String sanitizeForLog(String value) {
        if (value == null) {
            return null;
        }

        StringBuilder sanitized = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\r' -> sanitized.append("\\r");
                case '\n' -> sanitized.append("\\n");
                case '\t' -> sanitized.append("\\t");
                default -> sanitized.append(Character.isISOControl(character) ? '?' : character);
            }
        }
        return sanitized.toString();
    }

    public static boolean containsUnsafeLogCharacters(String value) {
        return value != null && !sanitizeForLog(value).equals(value);
    }

    public static Map<String, Object> sanitizeContextForLog(Map<String, ?> context) {
        Map<String, Object> sanitized = new LinkedHashMap<>();
        context.forEach((key, value) -> sanitized.put(sanitizeForLog(key), sanitizeLogValue(value)));
        return sanitized;
    }

    private static Object sanitizeValues(String[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 1) {
            return sanitizeForLog(values[0]);
        }
        return Arrays.stream(values).map(SensitiveDataSanitizer::sanitizeForLog).toList();
    }

    private static Object sanitizeLogValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String stringValue) {
            return sanitizeForLog(stringValue);
        }
        if (value instanceof Map<?, ?> mapValue) {
            Map<String, Object> sanitized = new LinkedHashMap<>();
            mapValue.forEach((key, nestedValue) -> sanitized.put(
                sanitizeForLog(String.valueOf(key)), sanitizeLogValue(nestedValue)
            ));
            return sanitized;
        }
        if (value instanceof Iterable<?> iterableValue) {
            List<Object> sanitized = new java.util.ArrayList<>();
            for (Object element : iterableValue) {
                sanitized.add(sanitizeLogValue(element));
            }
            return sanitized;
        }
        return sanitizeForLog(String.valueOf(value));
    }

    private static String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
    }
}
