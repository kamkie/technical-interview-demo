package team.jit.technicalinterviewdemo.business.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTests {

    @Mock
    private CurrentUserAccountService currentUserAccountService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ApplicationMetrics applicationMetrics;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountService(
                currentUserAccountService,
                userAccountRepository,
                applicationMetrics
        );
    }

    @Test
    void updatePreferredLanguageClearsBlankValue() {
        UserAccount currentUser = testUserAccount();
        currentUser.setPreferredLanguage("pl");
        when(currentUserAccountService.getCurrentUserOrSynchronize()).thenReturn(currentUser);
        when(userAccountRepository.saveAndFlush(currentUser)).thenReturn(currentUser);

        UserAccountResponse response = userAccountService.updatePreferredLanguage("   ");

        assertThat(response.preferredLanguage()).isNull();
        verify(userAccountRepository).saveAndFlush(currentUser);
    }

    @Test
    void updatePreferredLanguageRejectsInvalidFormat() {
        when(currentUserAccountService.getCurrentUserOrSynchronize()).thenReturn(testUserAccount());

        assertThatThrownBy(() -> userAccountService.updatePreferredLanguage("pol"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("preferredLanguage must be a two-letter ISO 639-1 code.");

        verify(userAccountRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updatePreferredLanguageRejectsUnsupportedLanguage() {
        when(currentUserAccountService.getCurrentUserOrSynchronize()).thenReturn(testUserAccount());

        assertThatThrownBy(() -> userAccountService.updatePreferredLanguage("it"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("preferredLanguage must be one of: en, es, de, fr, pl, uk, no.");

        verify(userAccountRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void getCurrentUserAccountSortsRolesAlphabetically() {
        UserAccount currentUser = new UserAccount(
                "github",
                "kamkie",
                "Kamil Kiewisz",
                "kamil@example.com",
                null,
                LocalDateTime.of(2026, 4, 30, 20, 0),
                Set.of(UserRole.USER, UserRole.ADMIN)
        );
        when(currentUserAccountService.getCurrentUserOrSynchronize()).thenReturn(currentUser);

        UserAccountResponse response = userAccountService.getCurrentUserAccount();

        assertThat(response.roles()).containsExactly("ADMIN", "USER");
    }

    private UserAccount testUserAccount() {
        return new UserAccount(
                "github",
                "kamkie",
                "Kamil Kiewisz",
                "kamil@example.com",
                null,
                LocalDateTime.of(2026, 4, 30, 20, 0),
                Set.of(UserRole.USER)
        );
    }
}

