package team.jit.technicalinterviewdemo.business.localization.seed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

@Slf4j
@Configuration
public class LocalizationDataInitializer {

    @Bean
    CommandLineRunner seedLocalizations(
            LocalizationRepository localizationMessageRepository,
            BootstrapSettingsProperties bootstrapSettingsProperties
    ) {
        return args -> {
            if (!bootstrapSettingsProperties.getSeed().isDemoData()) {
                log.info("Skipping demo localization bootstrap because app.bootstrap.seed.demo-data is disabled.");
                return;
            }
            for (Localization seedMessage : LocalizationSeedData.defaultMessages()) {
                if (localizationMessageRepository.existsByMessageKeyAndLanguage(seedMessage.getMessageKey(), seedMessage.getLanguage())) {
                    continue;
                }

                Localization savedMessage = localizationMessageRepository.save(seedMessage);
                log.info(
                        "Seeded localization message id={} key={} language={}",
                        savedMessage.getId(),
                        savedMessage.getMessageKey(),
                        savedMessage.getLanguage()
                );
            }
        };
    }
}
