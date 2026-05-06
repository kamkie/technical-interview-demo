package team.jit.technicalinterviewdemo.technical.security;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "SessionResponse", description = "Same-site browser session contract for the separate first-party UI.")
public record SessionResponse(
        @Schema(
                description = "Whether the current browser request is backed by an authenticated application session.",
                example = "true")
        boolean authenticated,

        @Schema(
                description = "Endpoint path for the authenticated persisted-account resource.",
                example = "/api/account")
        String accountPath,

        @Schema(
                description =
                        "Interactive login bootstrap options for the current runtime. It is an empty array when oauth"
                                + " is inactive.")
        List<LoginProvider> loginProviders,

        @Schema(description = "Same-site logout endpoint path.", example = "/api/session/logout")
        String logoutPath,

        @Schema(description = "Session cookie contract exposed for same-site browser clients.")
        SessionCookie sessionCookie,

        @Schema(description = "Current CSRF contract for browser writes.")
        Csrf csrf) {

    @Schema(name = "SessionLoginProvider", description = "Available same-site OAuth login bootstrap option.")
    public record LoginProvider(
            @Schema(description = "Configured OAuth client registration id.", example = "github")
            String registrationId,

            @Schema(
                    description = "Display name exposed by the configured OAuth client registration.",
                    example = "GitHub")
            String clientName,

            @Schema(
                    description = "Relative same-site authorization bootstrap path for the configured provider.",
                    example = "/api/session/oauth2/authorization/github")
            String authorizationPath) {}

    @Schema(
            name = "SessionCookieContract",
            description = "Session cookie settings relevant to same-site browser clients.")
    public record SessionCookie(
            @Schema(
                    description = "Session cookie name expected by protected operations.",
                    example = "technical-interview-demo-session")
            String name,

            @Schema(description = "Whether the session cookie is HTTP-only.", example = "true")
            boolean httpOnly,

            @Schema(description = "Session cookie SameSite mode.", example = "lax")
            String sameSite,

            @Schema(description = "Whether the session cookie requires HTTPS.", example = "false")
            boolean secure) {}

    @Schema(name = "SessionCsrfContract", description = "Current CSRF contract for browser writes.")
    public record Csrf(
            @Schema(description = "Whether CSRF protection is currently enabled for browser writes.", example = "true")
            boolean enabled,

            @Schema(
                    description = "Readable CSRF cookie name that the browser UI mirrors into the request header.",
                    example = "XSRF-TOKEN")
            String cookieName,

            @Schema(description = "Request header name required on unsafe browser writes.", example = "X-XSRF-TOKEN")
            String headerName) {}
}
