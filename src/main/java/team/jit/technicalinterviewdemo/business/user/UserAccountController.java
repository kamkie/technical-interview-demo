package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

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
        summary = "Get the current account", description = "Requires an authenticated session and returns the persisted application account.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<UserAccountResponse> currentUser() {
        UserAccountResponse payload = userAccountService.getCurrentUserAccount();
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/language")
    @Operation(
        summary = "Update the account language", description = "Requires an authenticated session and a valid same-site CSRF header mirrored from the readable XSRF-TOKEN cookie. Blank or null clears the preference.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    @Parameter(
        name = SameSiteCsrfContract.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie."
    )
    public ResponseEntity<UserAccountResponse> updatePreferredLanguage(
        @RequestBody UserAccountLanguageRequest request
    ) {
        UserAccountResponse payload = userAccountService.updatePreferredLanguage(request.preferredLanguage());
        return ResponseEntity.ok(payload);
    }
}
