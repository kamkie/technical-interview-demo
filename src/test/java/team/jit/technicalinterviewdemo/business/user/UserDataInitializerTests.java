package team.jit.technicalinterviewdemo.business.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataInitializerTests {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Test
    void seedUsersSkipsWhenDemoBootstrapIsDisabled() throws Exception {
        UserDataInitializer initializer = new UserDataInitializer();

        CommandLineRunner runner = initializer.seedUsers(userAccountRepository, bootstrapSettings(false));
        runner.run();

        verifyNoInteractions(userAccountRepository);
    }

    @Test
    void seedUsersWritesDefaultUsersWhenDemoBootstrapIsEnabled() throws Exception {
        UserDataInitializer initializer = new UserDataInitializer();
        when(userAccountRepository.existsByProviderAndExternalLogin(anyString(), anyString()))
                .thenReturn(false);
        when(userAccountRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = initializer.seedUsers(userAccountRepository, bootstrapSettings(true));
        runner.run();

        verify(userAccountRepository, times(500)).existsByProviderAndExternalLogin(eq("github"), anyString());
        verify(userAccountRepository).saveAll(argThat(users -> {
            List<UserAccount> savedUsers =
                    StreamSupport.stream(users.spliterator(), false).toList();
            assertThat(savedUsers).hasSize(500);
            assertThat(savedUsers.getFirst().getExternalLogin()).isEqualTo("demo-user-001");
            assertThat(savedUsers.getFirst().getDisplayName()).isEqualTo("Demo User 001");
            assertThat(savedUsers.getFirst().getEmail()).isEqualTo("demo-user-001@example.test");
            assertThat(savedUsers.getFirst().getRoles()).containsExactly(UserRole.USER);
            assertThat(savedUsers.getFirst().getRoleGrants())
                    .singleElement()
                    .extracting(UserRoleGrant::getGrantSource)
                    .isEqualTo(UserRoleGrantSource.AUTHENTICATED_LOGIN);
            assertThat(savedUsers.getLast().getExternalLogin()).isEqualTo("demo-user-500");
            return true;
        }));
        verifyNoMoreInteractions(userAccountRepository);
    }

    @Test
    void seedUsersOnlyWritesMissingDefaultUsers() throws Exception {
        UserDataInitializer initializer = new UserDataInitializer();
        when(userAccountRepository.existsByProviderAndExternalLogin(anyString(), anyString()))
                .thenReturn(false);
        when(userAccountRepository.existsByProviderAndExternalLogin("github", "demo-user-001"))
                .thenReturn(true);
        when(userAccountRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = initializer.seedUsers(userAccountRepository, bootstrapSettings(true));
        runner.run();

        verify(userAccountRepository).saveAll(argThat(users -> {
            List<UserAccount> savedUsers =
                    StreamSupport.stream(users.spliterator(), false).toList();
            assertThat(savedUsers)
                    .hasSize(499)
                    .noneMatch(user -> "demo-user-001".equals(user.getExternalLogin()))
                    .anyMatch(user -> "demo-user-500".equals(user.getExternalLogin()));
            return true;
        }));
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
