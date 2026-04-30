package team.jit.technicalinterviewdemo.business.user;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.localization.SupportedLanguages;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;
import team.jit.technicalinterviewdemo.technical.security.AuthenticatedUserSecurityService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final AuthenticatedUserSecurityService authenticatedUserSecurityService;
    private final UserAccountRepository userAccountRepository;
    private final ApplicationMetrics applicationMetrics;

    public UserProfileResponse getCurrentUserProfile() {
        UserAccount currentUser = authenticatedUserSecurityService.getCurrentUserOrSynchronize();
        applicationMetrics.recordUserOperation("getCurrentProfile");
        return UserProfileResponse.from(currentUser);
    }

    @Transactional
    public UserProfileResponse updatePreferredLanguage(String preferredLanguage) {
        UserAccount currentUser = authenticatedUserSecurityService.getCurrentUserOrSynchronize();
        currentUser.setPreferredLanguage(normalizePreferredLanguage(preferredLanguage));
        UserAccount updatedUser = userAccountRepository.saveAndFlush(currentUser);
        applicationMetrics.recordUserOperation("updatePreferredLanguage");
        log.info(
                "Updated user preferred language id={} login={} preferredLanguage={}"
                ,
                updatedUser.getId(),
                updatedUser.getExternalLogin(),
                updatedUser.getPreferredLanguage()
        );
        return UserProfileResponse.from(updatedUser);
    }

    private String normalizePreferredLanguage(String preferredLanguage) {
        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            return null;
        }

        String normalizedPreferredLanguage = preferredLanguage.trim().toLowerCase(Locale.ROOT);
        if (!normalizedPreferredLanguage.matches("^[a-z]{2}$")) {
            throw new InvalidRequestException("preferredLanguage must be a two-letter ISO 639-1 code.");
        }
        if (!SupportedLanguages.isSupported(normalizedPreferredLanguage)) {
            throw new InvalidRequestException(
                    "preferredLanguage must be one of: %s.".formatted(SupportedLanguages.description())
            );
        }
        return normalizedPreferredLanguage;
    }
}
