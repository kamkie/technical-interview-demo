package team.jit.technicalinterviewdemo.business.localization.seed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        when(localizationRepository.existsByMessageKeyAndLanguage(any(String.class), any(String.class)))
                .thenReturn(false);
        when(localizationRepository.save(any(Localization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Localization.class));

        CommandLineRunner runner = initializer.seedLocalizations(localizationRepository, bootstrapSettings(true));
        runner.run();

        verify(
                        localizationRepository,
                        times(LocalizationSeedData.defaultMessages().size()))
                .save(any(Localization.class));
    }

    private static BootstrapSettingsProperties bootstrapSettings(boolean demoDataEnabled) {
        BootstrapSettingsProperties properties = new BootstrapSettingsProperties();
        properties.getSeed().setDemoData(demoDataEnabled);
        return properties;
    }
}
