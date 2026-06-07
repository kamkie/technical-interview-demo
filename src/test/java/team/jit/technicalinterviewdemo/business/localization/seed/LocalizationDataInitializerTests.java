package team.jit.technicalinterviewdemo.business.localization.seed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalizationDataInitializerTests {

    @Mock
    private LocalizationRepository localizationRepository;

    @Test
    void seedLocalizationsSkipsWhenDemoBootstrapIsDisabled() throws Exception {
        LocalizationDataInitializer initializer = new LocalizationDataInitializer();

        CommandLineRunner runner = initializer.seedLocalizations(localizationRepository, bootstrapSettings(false));
        runner.run();

        verifyNoInteractions(localizationRepository);
    }

    @Test
    void seedLocalizationsWritesDefaultMessagesWhenDemoBootstrapIsEnabled() throws Exception {
        LocalizationDataInitializer initializer = new LocalizationDataInitializer();
        when(localizationRepository.findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection()))
                .thenReturn(List.of());
        when(localizationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = initializer.seedLocalizations(localizationRepository, bootstrapSettings(true));
        runner.run();

        verify(localizationRepository).findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection());
        verify(localizationRepository).saveAll(argThat(messages -> {
            assertThat(messages).hasSize(LocalizationSeedData.defaultMessages().size());
            return true;
        }));
        verifyNoMoreInteractions(localizationRepository);
    }

    @Test
    void seedLocalizationsUsesBulkLookupAndOnlyWritesMissingMessages() throws Exception {
        LocalizationDataInitializer initializer = new LocalizationDataInitializer();
        Localization existingMessage = LocalizationSeedData.defaultMessages().getFirst();
        when(localizationRepository.findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection()))
                .thenReturn(List.of(existingMessage));
        when(localizationRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = initializer.seedLocalizations(localizationRepository, bootstrapSettings(true));
        runner.run();

        verify(localizationRepository).findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection());
        verify(localizationRepository).saveAll(argThat(messages -> {
            List<Localization> savedMessages =
                    StreamSupport.stream(messages.spliterator(), false).toList();
            assertThat(savedMessages)
                    .hasSize(LocalizationSeedData.defaultMessages().size() - 1)
                    .noneMatch(message -> sameMessageIdentity(message, existingMessage));
            return true;
        }));
        verifyNoMoreInteractions(localizationRepository);
    }

    @Test
    void seedLocalizationsSkipsWriteWhenAllSeedMessagesExist() throws Exception {
        LocalizationDataInitializer initializer = new LocalizationDataInitializer();
        when(localizationRepository.findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection()))
                .thenReturn(LocalizationSeedData.defaultMessages());

        CommandLineRunner runner = initializer.seedLocalizations(localizationRepository, bootstrapSettings(true));
        runner.run();

        verify(localizationRepository).findAllByMessageKeyInAndLanguageIn(anyCollection(), anyCollection());
        verifyNoMoreInteractions(localizationRepository);
    }

    private static boolean sameMessageIdentity(Localization left, Localization right) {
        return left.getMessageKey().equals(right.getMessageKey())
                && left.getLanguage().equals(right.getLanguage());
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
