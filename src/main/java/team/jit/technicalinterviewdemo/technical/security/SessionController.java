package team.jit.technicalinterviewdemo.technical.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Tag(name = "Session", description = "Same-site browser session contract endpoints for the separate first-party UI.")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(
        summary = "Get the current browser session contract", description = "Public same-site session/bootstrap endpoint for the separate first-party UI. Returns the current authenticated state, persisted-account path, available OAuth login providers, logout path, session-cookie contract, and the readable CSRF cookie plus request-header names required for unsafe browser writes."
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Current same-site browser session state.")
    })
    public ResponseEntity<SessionResponse> currentSession(HttpServletRequest request, HttpServletResponse response) {
        SessionResponse payload = sessionService.currentSession(request, response);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Log out the current browser session", description = "Same-site logout endpoint for the separate first-party UI. Invalidates the current application session when present, clears both the session and readable CSRF cookies, requires a valid CSRF header when a current application session exists, and still returns 204 when no session exists."
    )
    @Parameter(
        name = SameSiteCsrfContract.HEADER_NAME, in = ParameterIn.HEADER, required = false, description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie when the browser currently has an authenticated application session."
    )
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Current session invalidated and cookie cleared.")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        sessionService.logoutCurrentSession(request, response);
        return ResponseEntity.noContent().build();
    }
}
