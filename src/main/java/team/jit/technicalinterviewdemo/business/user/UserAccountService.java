package team.jit.technicalinterviewdemo.business.user;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.localization.SupportedLanguages;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final CurrentUserAccountService currentUserAccountService;
    private final UserAccountRepository userAccountRepository;
    private final ApplicationMetrics applicationMetrics;

    public UserAccountResponse getCurrentUserAccount() {
        UserAccount currentUser = currentUserAccountService.getCurrentUserOrSynchronize();
        applicationMetrics.recordUserOperation("getCurrentProfile");
        return UserAccountResponse.from(currentUser);
    }

    @Transactional
    public UserAccountResponse updatePreferredLanguage(String preferredLanguage) {
        UserAccount currentUser = currentUserAccountService.getCurrentUserOrSynchronize();
        currentUser.setPreferredLanguage(normalizePreferredLanguage(preferredLanguage));
        UserAccount updatedUser = userAccountRepository.saveAndFlush(currentUser);
        applicationMetrics.recordUserOperation("updatePreferredLanguage");
        log.info(
                "Updated user preferred language id={} login={} preferredLanguage={}",
                updatedUser.getId(),
                updatedUser.getExternalLogin(),
                updatedUser.getPreferredLanguage()
        );
        return UserAccountResponse.from(updatedUser);
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

