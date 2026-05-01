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
@RequestMapping("/api/account")
@Tag(name = "Account", description = "Authenticated account endpoints.")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    @Operation(
            summary = "Get the current account",
            description = "Requires an authenticated session and returns the persisted application account.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<UserAccountResponse> currentUser() {
        UserAccountResponse payload = userAccountService.getCurrentUserAccount();
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/language")
    @Operation(
            summary = "Update the account language",
            description = "Requires an authenticated session. Blank or null clears the preference.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<UserAccountResponse> updatePreferredLanguage(
            @RequestBody UserAccountLanguageRequest request
    ) {
        UserAccountResponse payload = userAccountService.updatePreferredLanguage(request.preferredLanguage());
        return ResponseEntity.ok(payload);
    }
}

