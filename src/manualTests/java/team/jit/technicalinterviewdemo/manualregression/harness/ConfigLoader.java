package team.jit.technicalinterviewdemo.manualregression.harness;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * Resolves a {@link RunConfig} from the supported input precedence:
 *
 * <ol>
 *   <li>explicit Gradle property forwarded as a JVM system property ({@code -PmanualTests.foo=bar})
 *   <li>environment variable
 *   <li>{@code src/manualTests/resources/run.properties} when present on the classpath
 *   <li>interactive prompt (only when {@link System#console()} is non-null)
 * </ol>
 *
 * <p>Secrets read via prompt are read with {@link Console#readPassword(String, Object...)} so the
 * value is not echoed.
 */
public final class ConfigLoader {

    /** Required input keys. */
    public static final String KEY_BASE_URL = "baseUrl";

    public static final String KEY_ADMIN_SESSION_COOKIE = "adminSessionCookie";
    public static final String KEY_ADMIN_CSRF_TOKEN = "adminCsrfToken";

    /** Optional input keys. */
    public static final String KEY_REGULAR_SESSION_COOKIE = "regularSessionCookie";

    public static final String KEY_REGULAR_CSRF_TOKEN = "regularCsrfToken";
    public static final String KEY_REGULAR_USER_ID = "regularUserId";
    public static final String KEY_RUN_TAG = "runTag";
    public static final String KEY_SUITES = "suites";
    public static final String KEY_OUT_DIR = "outDir";
    public static final String KEY_ALLOWED_HOSTS = "allowedHosts";

    /** Suite names that require an admin identity to be runnable. */
    public static final Set<String> SUITES_REQUIRING_ADMIN = Set.of(
            "06-session-and-account",
            "07-book-lifecycle",
            "08-category-lifecycle-admin",
            "09-localization-lifecycle-admin",
            "10-admin-user-management",
            "11-audit-log-review",
            "12-operator-surface");

    private static final String SYSTEM_PROPERTY_PREFIX = "manualTests.";
    private static final String ENV_PREFIX = "MANUAL_TESTS_";
    private static final String CLASSPATH_PROPERTIES = "/run.properties";
    private static final String SECRET_PLACEHOLDER = "***";
    private static final char[] RUN_TAG_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private static final Set<String> SECRET_KEYS =
            Set.of(KEY_ADMIN_SESSION_COOKIE, KEY_ADMIN_CSRF_TOKEN, KEY_REGULAR_SESSION_COOKIE, KEY_REGULAR_CSRF_TOKEN);

    private ConfigLoader() {}

    /** Loads the configuration. Throws {@link ManualRegressionConfigException} on hard failures. */
    @SuppressWarnings("SystemConsoleNull")
    public static RunConfig load() {
        Properties propertiesFile = loadPropertiesFile();
        Console console = System.console();
        boolean interactive =
                console != null && !"true".equals(System.getProperty(SYSTEM_PROPERTY_PREFIX + "invokedFromGradle"));
        List<String> missingRequired = new ArrayList<>();

        // Required: base URL has a default and never blocks.
        String baseUrl = resolve(KEY_BASE_URL, propertiesFile, console, interactive, false)
                .orElse("http://localhost:8080");

        // Selected suites determines whether admin inputs are required at all.
        List<String> selectedSuites = resolve(KEY_SUITES, propertiesFile, null, false, false)
                .map(ConfigLoader::splitCsv)
                .orElse(List.of());
        boolean anyAdminSuiteSelected =
                selectedSuites.isEmpty() || selectedSuites.stream().anyMatch(SUITES_REQUIRING_ADMIN::contains);

        Optional<String> adminCookie = resolve(KEY_ADMIN_SESSION_COOKIE, propertiesFile, console, interactive, true);
        Optional<String> adminCsrf = resolve(KEY_ADMIN_CSRF_TOKEN, propertiesFile, console, interactive, true);
        if (anyAdminSuiteSelected) {
            if (adminCookie.isEmpty()) {
                missingRequired.add(KEY_ADMIN_SESSION_COOKIE);
            }
            if (adminCsrf.isEmpty()) {
                missingRequired.add(KEY_ADMIN_CSRF_TOKEN);
            }
        }

        Optional<String> regularCookie = resolve(KEY_REGULAR_SESSION_COOKIE, propertiesFile, null, false, true);
        Optional<String> regularCsrf = resolve(KEY_REGULAR_CSRF_TOKEN, propertiesFile, null, false, true);
        Optional<String> regularUserId = resolve(KEY_REGULAR_USER_ID, propertiesFile, null, false, false);

        String runTag =
                resolve(KEY_RUN_TAG, propertiesFile, null, false, false).orElseGet(ConfigLoader::generateRunTag);

        Path outputDirectory = resolve(KEY_OUT_DIR, propertiesFile, null, false, false)
                .map(Paths::get)
                .orElseGet(() -> defaultOutputDirectory(runTag));

        List<String> allowedHosts = resolve(KEY_ALLOWED_HOSTS, propertiesFile, null, false, false)
                .map(ConfigLoader::splitCsv)
                .orElse(List.of());

        Optional<String> activeProfileHint = Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"));

        if (!missingRequired.isEmpty()) {
            String message =
                    "Manual-regression harness is missing required inputs: " + String.join(", ", missingRequired)
                            + ". Provide them via -PmanualTests.<key>=<value>, MANUAL_TESTS_<KEY>=<value>, "
                            + "or src/manualTests/resources/run.properties (use run.properties.example as a template).";
            if (!interactive) {
                throw new ManualRegressionConfigException(message);
            }
            // In interactive mode, the resolve() helper has already prompted; missing here means the user pressed
            // enter.
            throw new ManualRegressionConfigException(message);
        }

        SafetyRails.assertSafeBaseUrl(baseUrl, allowedHosts);
        if (activeProfileHint
                .map(p -> p.toLowerCase(Locale.ROOT).contains("prod"))
                .orElse(false)) {
            throw new ManualRegressionConfigException(
                    "Refusing to run manual-regression harness against an environment with SPRING_PROFILES_ACTIVE="
                            + activeProfileHint.get()
                            + " (looks like production).");
        }

        return new RunConfig(
                baseUrl,
                adminCookie,
                adminCsrf,
                regularCookie,
                regularCsrf,
                regularUserId,
                runTag,
                selectedSuites,
                outputDirectory,
                allowedHosts,
                activeProfileHint);
    }

    /**
     * Returns the masked value for log/report rendering. Secret keys are replaced with a placeholder.
     */
    public static String maskIfSecret(String key, String value) {
        return SECRET_KEYS.contains(key) && value != null && !value.isBlank() ? SECRET_PLACEHOLDER : value;
    }

    private static Optional<String> resolve(
            String key, Properties propertiesFile, Console console, boolean interactive, boolean secret) {
        String fromSystem = System.getProperty(SYSTEM_PROPERTY_PREFIX + key);
        if (isPresent(fromSystem)) {
            return Optional.of(fromSystem.trim());
        }
        String fromEnv = System.getenv(ENV_PREFIX + camelToScreamingSnake(key));
        if (isPresent(fromEnv)) {
            return Optional.of(fromEnv.trim());
        }
        String fromProperties = propertiesFile.getProperty(key);
        if (isPresent(fromProperties)) {
            return Optional.of(fromProperties.trim());
        }
        if (interactive && console != null) {
            String prompt = "manualTests." + key + (secret ? " (input hidden): " : ": ");
            String value = secret ? new String(console.readPassword(prompt)) : console.readLine(prompt);
            if (isPresent(value)) {
                return Optional.of(value.trim());
            }
        }
        return Optional.empty();
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private static List<String> splitCsv(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        Set<String> deduplicated = new LinkedHashSet<>();
        for (String part : value.split(",", -1)) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                deduplicated.add(trimmed);
            }
        }
        return List.copyOf(deduplicated);
    }

    private static String camelToScreamingSnake(String camel) {
        StringBuilder sb = new StringBuilder(camel.length() + 4);
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_').append(c);
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    private static Properties loadPropertiesFile() {
        Properties properties = new Properties();
        try (InputStream stream = ConfigLoader.class.getResourceAsStream(CLASSPATH_PROPERTIES)) {
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException ex) {
            throw new ManualRegressionConfigException("Failed to read run.properties", ex);
        }
        return properties;
    }

    private static String generateRunTag() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                .withZone(java.time.ZoneOffset.UTC)
                .format(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        SecureRandom random = new SecureRandom();
        StringBuilder suffix = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffix.append(RUN_TAG_ALPHABET[random.nextInt(RUN_TAG_ALPHABET.length)]);
        }
        return "manual-" + timestamp + "-" + suffix;
    }

    private static Path defaultOutputDirectory(String runTag) {
        // Reports default to ai/tmp/manual-regression/run-<runTag>/ relative to the project root,
        // which is the working directory when Gradle launches the task.
        Path base = Paths.get("ai", "tmp", "manual-regression", "run-" + runTag);
        try {
            Files.createDirectories(base);
        } catch (IOException ex) {
            throw new ManualRegressionConfigException("Failed to create report directory: " + base, ex);
        }
        return base.toAbsolutePath();
    }
}
