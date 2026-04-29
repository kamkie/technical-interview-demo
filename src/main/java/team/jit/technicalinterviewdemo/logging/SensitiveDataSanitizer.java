package team.jit.technicalinterviewdemo.logging;

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

    private static String normalize(String value) {
        return value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }
}
