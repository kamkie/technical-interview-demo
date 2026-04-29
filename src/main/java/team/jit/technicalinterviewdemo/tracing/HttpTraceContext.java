package team.jit.technicalinterviewdemo.tracing;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

public record HttpTraceContext(
        String version,
        String traceId,
        String spanId,
        String traceFlags
) {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final HexFormat HEX_FORMAT = HexFormat.of();
    private static final String DEFAULT_VERSION = "00";
    private static final String DEFAULT_TRACE_FLAGS = "01";

    public static HttpTraceContext create(String traceparentHeader) {
        ParsedTraceparent parsedTraceparent = parse(traceparentHeader).orElse(null);
        if (parsedTraceparent == null) {
            return new HttpTraceContext(
                    DEFAULT_VERSION,
                    randomHex(16),
                    randomHex(8),
                    DEFAULT_TRACE_FLAGS
            );
        }

        return new HttpTraceContext(
                parsedTraceparent.version(),
                parsedTraceparent.traceId(),
                randomHex(8),
                parsedTraceparent.traceFlags()
        );
    }

    public String traceparent() {
        return "%s-%s-%s-%s".formatted(version, traceId, spanId, traceFlags);
    }

    private static Optional<ParsedTraceparent> parse(String traceparentHeader) {
        if (traceparentHeader == null || traceparentHeader.isBlank()) {
            return Optional.empty();
        }

        String[] parts = traceparentHeader.trim().toLowerCase(Locale.ROOT).split("-");
        if (parts.length != 4) {
            return Optional.empty();
        }

        String version = parts[0];
        String traceId = parts[1];
        String parentSpanId = parts[2];
        String traceFlags = parts[3];

        if (!isLowerHex(version, 2)
                || !isLowerHex(traceId, 32)
                || !isLowerHex(parentSpanId, 16)
                || !isLowerHex(traceFlags, 2)
                || traceId.equals("00000000000000000000000000000000")
                || parentSpanId.equals("0000000000000000")) {
            return Optional.empty();
        }

        return Optional.of(new ParsedTraceparent(version, traceId, traceFlags));
    }

    private static boolean isLowerHex(String value, int expectedLength) {
        return value != null && value.matches("[0-9a-f]{" + expectedLength + "}");
    }

    private static String randomHex(int bytes) {
        byte[] buffer = new byte[bytes];
        SECURE_RANDOM.nextBytes(buffer);
        return HEX_FORMAT.formatHex(buffer);
    }

    private record ParsedTraceparent(
            String version,
            String traceId,
            String traceFlags
    ) {
    }
}
