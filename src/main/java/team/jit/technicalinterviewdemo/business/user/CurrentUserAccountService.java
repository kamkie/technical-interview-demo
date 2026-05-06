package team.jit.technicalinterviewdemo.business.user;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.technical.api.ForbiddenOperationException;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CurrentUserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ApplicationMetrics applicationMetrics;
    private final BootstrapSettingsProperties bootstrapSettingsProperties;

    @Transactional
    public UserAccount synchronizeCurrentAuthenticatedUser() {
        AuthenticatedUserDetails authenticatedUser = currentAuthenticatedUser().orElseThrow(() -> new ForbiddenOperationException("Authenticated user information is not available."));
        boolean shouldBootstrapAdmin = shouldBootstrapAdmin(authenticatedUser);

        UserAccount userAccount = userAccountRepository.findByProviderAndExternalLogin(
                authenticatedUser.provider(), authenticatedUser.login()
        ).orElseGet(() -> new UserAccount(
                authenticatedUser.provider(), authenticatedUser.login(), authenticatedUser.displayName(), authenticatedUser.email(), null, Instant.now(), java.util.Set.of(UserRole.USER)
        ));

        boolean created = userAccount.getId() == null;
        userAccount.setDisplayName(authenticatedUser.displayName());
        userAccount.setEmail(authenticatedUser.email());
        userAccount.setLastLoginAt(Instant.now());
        userAccount.ensureRoleGrant(UserRole.USER, UserRoleGrantSource.AUTHENTICATED_LOGIN, null, null);
        if (shouldBootstrapAdmin) {
            userAccount.ensureRoleGrant(UserRole.ADMIN, UserRoleGrantSource.BOOTSTRAP, null, null);
        }

        UserAccount savedUser = userAccountRepository.saveAndFlush(userAccount);
        applicationMetrics.recordUserOperation(created ? "create" : "loginSync");
        log.info(
                "Synchronized authenticated user id={} provider={} login={} roles={}", savedUser.getId(), savedUser.getProvider(), savedUser.getExternalLogin(), savedUser.getRoles()
        );
        return savedUser;
    }

    public Optional<UserAccount> findCurrentUser() {
        return currentAuthenticatedUser().flatMap(authenticatedUser -> userAccountRepository.findByProviderAndExternalLogin(
                authenticatedUser.provider(), authenticatedUser.login()
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
        return currentAuthenticatedUser().map(AuthenticatedUserDetails::identityKey);
    }

    private Optional<AuthenticatedUserDetails> currentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) || !oauth2AuthenticationToken.isAuthenticated()) {
            return Optional.empty();
        }

        OAuth2User principal = oauth2AuthenticationToken.getPrincipal();
        String login = normalizeRequiredPrincipalValue(
                findAttribute(principal, "login").or(() -> findAttribute(principal, "preferred_username")).or(() -> findAttribute(principal, "email")).orElse(principal.getName()), "login"
        );
        String displayName = findAttribute(principal, "name").orElse(login);
        String email = findAttribute(principal, "email").orElse(null);
        return Optional.of(new AuthenticatedUserDetails(
                oauth2AuthenticationToken.getAuthorizedClientRegistrationId().toLowerCase(Locale.ROOT), login, displayName, email
        ));
    }

    private Optional<String> findAttribute(OAuth2User principal, String attributeName) {
        Object value = principal.getAttributes().get(attributeName);
        if (!(value instanceof String stringValue) || stringValue.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(stringValue.trim());
    }

    private boolean shouldBootstrapAdmin(AuthenticatedUserDetails authenticatedUser) {
        return userAccountRepository.countByRole(UserRole.ADMIN) == 0 && bootstrapSettingsProperties.normalizedInitialAdminIdentities().contains(authenticatedUser.identityKey());
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
        private String identityKey() {
            return "%s:%s".formatted(provider, login);
        }
    }
}
