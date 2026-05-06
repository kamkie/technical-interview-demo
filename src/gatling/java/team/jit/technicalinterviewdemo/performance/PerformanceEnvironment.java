package team.jit.technicalinterviewdemo.performance;

final class PerformanceEnvironment {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private PerformanceEnvironment() {}

    static String baseUrl() {
        return System.getProperty("app.baseUrl", DEFAULT_BASE_URL);
    }

    static String sessionCookie() {
        return System.getProperty("app.sessionCookie", "").trim();
    }

    static boolean hasSessionCookie() {
        return !sessionCookie().isEmpty();
    }
}
