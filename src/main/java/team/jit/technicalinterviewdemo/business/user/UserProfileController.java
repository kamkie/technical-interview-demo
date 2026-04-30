package team.jit.technicalinterviewdemo.business.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserAccountService userAccountService;

    public UserProfileController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> currentUser() {
        UserProfileResponse payload = userAccountService.getCurrentUserProfile();
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/preferred-language")
    public ResponseEntity<UserProfileResponse> updatePreferredLanguage(
            @RequestBody UserLanguagePreferenceRequest request
    ) {
        UserProfileResponse payload = userAccountService.updatePreferredLanguage(request.preferredLanguage());
        return ResponseEntity.ok(payload);
    }
}
