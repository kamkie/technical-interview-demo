package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@RestController
@RequestMapping("/api/users/me")
@Tag(name = "Users", description = "Authenticated-user profile endpoints.")
public class UserProfileController {

    private final UserAccountService userAccountService;

    public UserProfileController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    @Operation(
            summary = "Get the current user profile",
            description = "Requires an authenticated session and returns the persisted application user profile.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<UserProfileResponse> currentUser() {
        UserProfileResponse payload = userAccountService.getCurrentUserProfile();
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/preferred-language")
    @Operation(
            summary = "Update the current user's preferred language",
            description = "Requires an authenticated session. Blank or null clears the preference.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<UserProfileResponse> updatePreferredLanguage(
            @RequestBody UserLanguagePreferenceRequest request
    ) {
        UserProfileResponse payload = userAccountService.updatePreferredLanguage(request.preferredLanguage());
        return ResponseEntity.ok(payload);
    }
}
