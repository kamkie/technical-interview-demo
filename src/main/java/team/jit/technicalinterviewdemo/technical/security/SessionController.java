package team.jit.technicalinterviewdemo.technical.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
            summary = "Get the current browser session contract",
            description = "Public same-site session/bootstrap endpoint for the separate first-party UI. Returns the current authenticated state, persisted-account path, available OAuth login providers, logout path, cookie contract, and current CSRF mode."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current same-site browser session state.")
    })
    public ResponseEntity<SessionResponse> currentSession(HttpServletRequest request) {
        return ResponseEntity.ok(sessionService.currentSession(request));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out the current browser session",
            description = "Same-site logout endpoint for the separate first-party UI. Invalidates the current application session if present, clears the session cookie, and returns 204 even when no session exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Current session invalidated and cookie cleared.")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, sessionService.logoutCurrentSession(request).toString())
                .build();
    }
}
