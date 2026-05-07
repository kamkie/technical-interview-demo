package team.jit.technicalinterviewdemo.manualregression.harness;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Boundary checks that prevent the harness from talking to anything outside a known-safe target.
 *
 * <p>The base URL must resolve to localhost, a private IPv4 range (10/8, 172.16/12, 192.168/16), an
 * IPv6 link-local/unique-local address, or a host explicitly added to the {@code allowedHosts}
 * list. The harness is intentionally strict: production-ish targets are refused before any HTTP
 * call is made.
 */
public final class SafetyRails {

    private SafetyRails() {}

    public static void assertSafeBaseUrl(String baseUrl, List<String> allowedHosts) {
        URI uri;
        try {
            uri = URI.create(baseUrl);
        } catch (IllegalArgumentException ex) {
            throw new ManualRegressionConfigException("BASE_URL is not a valid URI: " + baseUrl, ex);
        }
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new ManualRegressionConfigException("BASE_URL has no host: " + baseUrl);
        }
        if (allowedHosts.contains(host)) {
            return;
        }
        if ("localhost".equalsIgnoreCase(host)) {
            return;
        }
        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            throw new ManualRegressionConfigException(
                    "BASE_URL host is not resolvable: " + host
                            + ". Add it to MANUAL_TESTS_ALLOWED_HOSTS if it is intentionally external.",
                    ex);
        }
        if (address.isLoopbackAddress() || address.isSiteLocalAddress() || address.isLinkLocalAddress()) {
            return;
        }
        throw new ManualRegressionConfigException("Refusing to run against non-private host " + host + " ("
                + address.getHostAddress() + "). Add it to MANUAL_TESTS_ALLOWED_HOSTS to override.");
    }
}
