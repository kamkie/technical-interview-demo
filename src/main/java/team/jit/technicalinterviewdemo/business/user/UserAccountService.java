package team.jit.technicalinterviewdemo.business.user;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.technical.api.ForbiddenOperationException;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.business.localization.SupportedLanguages;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ApplicationMetrics applicationMetrics;

    @Value("${app.security.admin-logins:${ADMIN_LOGINS:}}")
    private String configuredAdminLogins;

    @Transactional
    public UserAccount synchronizeCurrentAuthenticatedUser() {
        AuthenticatedUserDetails authenticatedUser = currentAuthenticatedUser()
                .orElseThrow(() -> new ForbiddenOperationException("Authenticated user information is not available."));

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin(
                        authenticatedUser.provider(),
                        authenticatedUser.login()
                )
                .orElseGet(() -> new UserAccount(
                        authenticatedUser.provider(),
                        authenticatedUser.login(),
                        authenticatedUser.displayName(),
                        authenticatedUser.email(),
                        null,
                        LocalDateTime.now(ZoneOffset.UTC),
                        determineRoles(authenticatedUser.login())
                ));

        boolean created = userAccount.getId() == null;
        userAccount.setDisplayName(authenticatedUser.displayName());
        userAccount.setEmail(authenticatedUser.email());
        userAccount.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
        userAccount.setRoles(determineRoles(authenticatedUser.login()));

        UserAccount savedUser = userAccountRepository.saveAndFlush(userAccount);
        applicationMetrics.recordUserOperation(created ? "create" : "loginSync");
        log.info(
                "Synchronized authenticated user id={} provider={} login={} roles={}",
                savedUser.getId(),
                savedUser.getProvider(),
                savedUser.getExternalLogin(),
                savedUser.getRoles()
        );
        return savedUser;
    }

    public Optional<UserAccount> findCurrentUser() {
        return currentAuthenticatedUser()
                .flatMap(authenticatedUser -> userAccountRepository.findByProviderAndExternalLogin(
                        authenticatedUser.provider(),
                        authenticatedUser.login()
                ));
    }

    @Transactional
    public UserAccount getCurrentUserOrSynchronize() {
        Optional<UserAccount> currentUser = findCurrentUser();
        if (currentUser.isPresent()) {
            return currentUser.get();
        }
        return synchronizeCurrentAuthenticatedUser();
    }

    public UserProfileResponse getCurrentUserProfile() {
        UserAccount currentUser = getCurrentUserOrSynchronize();
        applicationMetrics.recordUserOperation("getCurrentProfile");
        return UserProfileResponse.from(currentUser);
    }

    @Transactional
    public UserProfileResponse updatePreferredLanguage(String preferredLanguage) {
        UserAccount currentUser = getCurrentUserOrSynchronize();
        currentUser.setPreferredLanguage(normalizePreferredLanguage(preferredLanguage));
        UserAccount updatedUser = userAccountRepository.saveAndFlush(currentUser);
        applicationMetrics.recordUserOperation("updatePreferredLanguage");
        log.info(
                "Updated user preferred language id={} login={} preferredLanguage={}",
                updatedUser.getId(),
                updatedUser.getExternalLogin(),
                updatedUser.getPreferredLanguage()
        );
        return UserProfileResponse.from(updatedUser);
    }

    public Optional<String> findCurrentUserPreferredLanguage() {
        return findCurrentUser().map(UserAccount::getPreferredLanguage);
    }

    public void requireRole(UserRole role, String message) {
        UserAccount currentUser = getCurrentUserOrSynchronize();
        if (!currentUser.getRoles().contains(role)) {
            throw new ForbiddenOperationException(message);
        }
    }

    public Optional<String> currentAuthenticatedUserKey() {
        return currentAuthenticatedUser().map(details -> "%s:%s".formatted(details.provider(), details.login()));
    }

    private Optional<AuthenticatedUserDetails> currentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken)
                || !oauth2AuthenticationToken.isAuthenticated()) {
            return Optional.empty();
        }

        OAuth2User principal = oauth2AuthenticationToken.getPrincipal();
        String login = normalizeRequiredPrincipalValue(
                findAttribute(principal, "login")
                        .or(() -> findAttribute(principal, "preferred_username"))
                        .or(() -> findAttribute(principal, "email"))
                        .orElse(principal.getName()),
                "login"
        );
        String displayName = findAttribute(principal, "name").orElse(login);
        String email = findAttribute(principal, "email").orElse(null);
        return Optional.of(new AuthenticatedUserDetails(
                oauth2AuthenticationToken.getAuthorizedClientRegistrationId().toLowerCase(Locale.ROOT),
                login,
                displayName,
                email
        ));
    }

    private Optional<String> findAttribute(OAuth2User principal, String attributeName) {
        Object value = principal.getAttributes().get(attributeName);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(stringValue.trim());
    }

    private Set<UserRole> determineRoles(String login) {
        Set<UserRole> roles = new LinkedHashSet<>();
        roles.add(UserRole.USER);
        if (adminLogins().contains(login.toLowerCase(Locale.ROOT))) {
            roles.add(UserRole.ADMIN);
        }
        return roles;
    }

    private Set<String> adminLogins() {
        if (configuredAdminLogins == null || configuredAdminLogins.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(configuredAdminLogins.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
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

    private String normalizeRequiredPrincipalValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ForbiddenOperationException("Authenticated user %s is not available.".formatted(fieldName));
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private record AuthenticatedUserDetails(
            String provider,
            String login,
            String displayName,
            String email
    ) {
    }
}
